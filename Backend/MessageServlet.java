import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/MessageServlet")
public class MessageServlet extends HttpServlet {

    private static final int MAX_MESSAGE_LENGTH = 1000;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        HttpSession session = request.getSession(false);

        if (!hasLoggedInUser(session)) {
            sendUnauthorized(response);
            return;
        }

        String userEmail = clean((String) session.getAttribute("userEmail"));
        String accountType = normalizeAccountType((String) session.getAttribute("accountType"));

        if (accountType.isEmpty()) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Student or employer account required");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database connection failed");
                return;
            }

            DatabaseSchemaManager.ensureJobsTable(con);
            DatabaseSchemaManager.ensureMessagingTables(con);

            int conversationId = parseId(request.getParameter("conversationId"));

            if (conversationId > 0) {
                writeConversationDetail(con, response, conversationId, userEmail, accountType);
                return;
            }

            int unreadCount = countUnreadMessages(con, userEmail, accountType);

            response.getWriter().write(
                    "{"
                            + "\"loggedIn\":true,"
                            + "\"accountType\":\"" + accountType + "\","
                            + "\"unreadCount\":" + unreadCount + ","
                            + "\"conversations\":" + listConversations(con, userEmail, accountType)
                            + "}"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load messages");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        prepareJsonResponse(response);

        HttpSession session = request.getSession(false);

        if (!hasLoggedInUser(session)) {
            sendUnauthorized(response);
            return;
        }

        String userEmail = clean((String) session.getAttribute("userEmail"));
        String accountType = normalizeAccountType((String) session.getAttribute("accountType"));

        if (accountType.isEmpty()) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Student or employer account required");
            return;
        }

        String action = clean(request.getParameter("action"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database connection failed");
                return;
            }

            DatabaseSchemaManager.ensureJobsTable(con);
            DatabaseSchemaManager.ensureMessagingTables(con);

            if ("start".equalsIgnoreCase(action)) {
                startConversation(con, request, response, userEmail, accountType);
                return;
            }

            if ("send".equalsIgnoreCase(action)) {
                sendMessage(con, request, response, userEmail, accountType);
                return;
            }

            if ("read".equalsIgnoreCase(action)) {
                markConversationRead(con, request, response, userEmail, accountType);
                return;
            }

            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported message action");
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update messages");
        }
    }

    private void startConversation(Connection con,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   String employerEmail,
                                   String accountType)
            throws SQLException, IOException {

        if (!"Employer".equals(accountType)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Only employers can start applicant conversations");
            return;
        }

        int applicationId = parseId(request.getParameter("applicationId"));

        if (applicationId <= 0) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Application id is required");
            return;
        }

        ApplicationSummary application = getEmployerApplication(con, applicationId, employerEmail);

        if (application == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Application not found for this employer");
            return;
        }

        int conversationId = getConversationIdByApplication(con, applicationId);

        if (conversationId <= 0) {
            conversationId = createConversation(con, application);
        }

        response.getWriter().write(
                "{\"success\":true,\"conversationId\":" + conversationId + "}"
        );
    }

    private void sendMessage(Connection con,
                             HttpServletRequest request,
                             HttpServletResponse response,
                             String userEmail,
                             String accountType)
            throws SQLException, IOException {

        int conversationId = parseId(request.getParameter("conversationId"));
        String body = clean(request.getParameter("body"));

        if (conversationId <= 0) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Conversation id is required");
            return;
        }

        if (body.isEmpty()) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Message cannot be empty");
            return;
        }

        if (body.length() > MAX_MESSAGE_LENGTH) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Message is too long");
            return;
        }

        ConversationSummary conversation = getAuthorizedConversation(
                con, conversationId, userEmail, accountType);

        if (conversation == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Conversation not found");
            return;
        }

        int messageId = insertMessage(con, conversationId, userEmail, accountType, body);
        updateLastMessageTime(con, conversationId);
        createMessageNotification(con, conversation, userEmail, accountType);

        response.getWriter().write(
                "{\"success\":true,\"messageId\":" + messageId + "}"
        );
    }

    private void markConversationRead(Connection con,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      String userEmail,
                                      String accountType)
            throws SQLException, IOException {

        int conversationId = parseId(request.getParameter("conversationId"));

        if (conversationId <= 0) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Conversation id is required");
            return;
        }

        ConversationSummary conversation = getAuthorizedConversation(
                con, conversationId, userEmail, accountType);

        if (conversation == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Conversation not found");
            return;
        }

        String sql = "UPDATE messages SET read_at = CURRENT_TIMESTAMP " +
                "WHERE conversation_id = ? AND sender_email <> ? AND read_at IS NULL";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setString(2, userEmail);
            ps.executeUpdate();
        }

        response.getWriter().write("{\"success\":true}");
    }

    private String listConversations(Connection con,
                                     String userEmail,
                                     String accountType) throws SQLException {

        String ownerColumn = "Employer".equals(accountType) ? "c.employer_email" : "c.student_email";

        String sql = "SELECT c.id, c.application_id, c.job_id, c.employer_email, " +
                "c.student_email, c.last_message_at, j.title, j.company, " +
                "(SELECT body FROM messages m WHERE m.conversation_id = c.id " +
                "ORDER BY m.created_at DESC, m.id DESC LIMIT 1) AS last_message, " +
                "(SELECT created_at FROM messages m WHERE m.conversation_id = c.id " +
                "ORDER BY m.created_at DESC, m.id DESC LIMIT 1) AS last_message_created_at, " +
                "(SELECT COUNT(*) FROM messages m WHERE m.conversation_id = c.id " +
                "AND m.sender_email <> ? AND m.read_at IS NULL) AS unread_count " +
                "FROM conversations c " +
                "JOIN jobs j ON j.id = c.job_id " +
                "WHERE " + ownerColumn + " = ? " +
                "ORDER BY COALESCE(c.last_message_at, c.created_at) DESC, c.id DESC " +
                "LIMIT 20";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, userEmail);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder json = new StringBuilder();
                json.append("[");

                boolean first = true;

                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }

                    String otherEmail = "Employer".equals(accountType)
                            ? clean(rs.getString("student_email"))
                            : clean(rs.getString("employer_email"));

                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"applicationId\":").append(rs.getInt("application_id")).append(",");
                    json.append("\"jobId\":").append(rs.getInt("job_id")).append(",");
                    json.append("\"jobTitle\":\"").append(escapeJson(rs.getString("title"))).append("\",");
                    json.append("\"company\":\"").append(escapeJson(rs.getString("company"))).append("\",");
                    json.append("\"studentEmail\":\"").append(escapeJson(rs.getString("student_email"))).append("\",");
                    json.append("\"employerEmail\":\"").append(escapeJson(rs.getString("employer_email"))).append("\",");
                    json.append("\"otherEmail\":\"").append(escapeJson(otherEmail)).append("\",");
                    json.append("\"lastMessage\":\"").append(escapeJson(rs.getString("last_message"))).append("\",");
                    json.append("\"lastMessageAt\":\"").append(escapeJson(firstNonEmpty(
                            rs.getString("last_message_created_at"),
                            rs.getString("last_message_at")
                    ))).append("\",");
                    json.append("\"unreadCount\":").append(rs.getInt("unread_count"));
                    json.append("}");

                    first = false;
                }

                json.append("]");
                return json.toString();
            }
        }
    }

    private void writeConversationDetail(Connection con,
                                         HttpServletResponse response,
                                         int conversationId,
                                         String userEmail,
                                         String accountType)
            throws SQLException, IOException {

        ConversationSummary conversation = getAuthorizedConversation(
                con, conversationId, userEmail, accountType);

        if (conversation == null) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND,
                    "Conversation not found");
            return;
        }

        response.getWriter().write(
                "{"
                        + "\"loggedIn\":true,"
                        + "\"conversation\":" + conversationToJson(conversation, accountType) + ","
                        + "\"messages\":" + listMessages(con, conversationId)
                        + "}"
        );
    }

    private String listMessages(Connection con,
                                int conversationId) throws SQLException {

        String sql = "SELECT id, sender_email, sender_account_type, body, created_at, read_at " +
                "FROM messages WHERE conversation_id = ? ORDER BY created_at ASC, id ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, conversationId);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder json = new StringBuilder();
                json.append("[");

                boolean first = true;

                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }

                    json.append("{");
                    json.append("\"id\":").append(rs.getInt("id")).append(",");
                    json.append("\"senderEmail\":\"").append(escapeJson(rs.getString("sender_email"))).append("\",");
                    json.append("\"senderAccountType\":\"").append(escapeJson(rs.getString("sender_account_type"))).append("\",");
                    json.append("\"body\":\"").append(escapeJson(rs.getString("body"))).append("\",");
                    json.append("\"createdAt\":\"").append(escapeJson(rs.getString("created_at"))).append("\",");
                    json.append("\"readAt\":\"").append(escapeJson(rs.getString("read_at"))).append("\"");
                    json.append("}");

                    first = false;
                }

                json.append("]");
                return json.toString();
            }
        }
    }

    private ApplicationSummary getEmployerApplication(Connection con,
                                                       int applicationId,
                                                       String employerEmail)
            throws SQLException {

        String sql = "SELECT a.id AS application_id, a.job_id, a.student_email, " +
                "j.employer_email, j.title, j.company " +
                "FROM applications a JOIN jobs j ON j.id = a.job_id " +
                "WHERE a.id = ? AND j.employer_email = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ps.setString(2, employerEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ApplicationSummary(
                            rs.getInt("application_id"),
                            rs.getInt("job_id"),
                            clean(rs.getString("employer_email")),
                            clean(rs.getString("student_email")),
                            clean(rs.getString("title")),
                            clean(rs.getString("company"))
                    );
                }
            }
        }

        return null;
    }

    private int getConversationIdByApplication(Connection con,
                                               int applicationId) throws SQLException {

        String sql = "SELECT id FROM conversations WHERE application_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        }
    }

    private int createConversation(Connection con,
                                   ApplicationSummary application) throws SQLException {

        String sql = "INSERT INTO conversations " +
                "(application_id, job_id, employer_email, student_email) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, application.applicationId);
            ps.setInt(2, application.jobId);
            ps.setString(3, application.employerEmail);
            ps.setString(4, application.studentEmail);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return getConversationIdByApplication(con, application.applicationId);
    }

    private ConversationSummary getAuthorizedConversation(Connection con,
                                                          int conversationId,
                                                          String userEmail,
                                                          String accountType)
            throws SQLException {

        String ownerColumn = "Employer".equals(accountType) ? "c.employer_email" : "c.student_email";

        String sql = "SELECT c.id, c.application_id, c.job_id, c.employer_email, " +
                "c.student_email, c.created_at, c.last_message_at, j.title, j.company, " +
                "j.location, j.category, j.type, a.cv_link, a.created_at AS applied_at " +
                "FROM conversations c " +
                "JOIN jobs j ON j.id = c.job_id " +
                "JOIN applications a ON a.id = c.application_id " +
                "WHERE c.id = ? AND " + ownerColumn + " = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setString(2, userEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ConversationSummary(
                            rs.getInt("id"),
                            rs.getInt("application_id"),
                            rs.getInt("job_id"),
                            clean(rs.getString("employer_email")),
                            clean(rs.getString("student_email")),
                            clean(rs.getString("title")),
                            clean(rs.getString("company")),
                            clean(rs.getString("location")),
                            clean(rs.getString("category")),
                            clean(rs.getString("type")),
                            clean(rs.getString("cv_link")),
                            clean(rs.getString("applied_at")),
                            clean(rs.getString("created_at")),
                            clean(rs.getString("last_message_at"))
                    );
                }
            }
        }

        return null;
    }

    private int insertMessage(Connection con,
                              int conversationId,
                              String senderEmail,
                              String senderAccountType,
                              String body) throws SQLException {

        String sql = "INSERT INTO messages " +
                "(conversation_id, sender_email, sender_account_type, body) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conversationId);
            ps.setString(2, senderEmail);
            ps.setString(3, senderAccountType);
            ps.setString(4, body);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    private void updateLastMessageTime(Connection con,
                                       int conversationId) throws SQLException {

        String sql = "UPDATE conversations SET last_message_at = CURRENT_TIMESTAMP " +
                "WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.executeUpdate();
        }
    }

    private int countUnreadMessages(Connection con,
                                    String userEmail,
                                    String accountType) throws SQLException {

        String ownerColumn = "Employer".equals(accountType) ? "c.employer_email" : "c.student_email";

        String sql = "SELECT COUNT(*) FROM messages m " +
                "JOIN conversations c ON c.id = m.conversation_id " +
                "WHERE " + ownerColumn + " = ? " +
                "AND m.sender_email <> ? AND m.read_at IS NULL";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, userEmail);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private void createMessageNotification(Connection con,
                                           ConversationSummary conversation,
                                           String senderEmail,
                                           String senderAccountType) {

        String recipientEmail = "Employer".equals(senderAccountType)
                ? conversation.studentEmail
                : conversation.employerEmail;
        String recipientAccountType = "Employer".equals(senderAccountType)
                ? "Student"
                : "Employer";
        String senderLabel = "Employer".equals(senderAccountType)
                ? "Employer"
                : "Student";

        try {
            NotificationService.createNotification(
                    con,
                    recipientEmail,
                    recipientAccountType,
                    "new_message",
                    "New message",
                    senderLabel + " sent you a message about " + conversation.jobTitle + ".",
                    "/frontend/messages.html?conversationId=" + conversation.id,
                    conversation.jobId,
                    conversation.applicationId
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String conversationToJson(ConversationSummary conversation,
                                      String accountType) {

        String otherEmail = "Employer".equals(accountType)
                ? conversation.studentEmail
                : conversation.employerEmail;

        return "{"
                + "\"id\":" + conversation.id + ","
                + "\"applicationId\":" + conversation.applicationId + ","
                + "\"jobId\":" + conversation.jobId + ","
                + "\"jobTitle\":\"" + escapeJson(conversation.jobTitle) + "\","
                + "\"company\":\"" + escapeJson(conversation.company) + "\","
                + "\"location\":\"" + escapeJson(conversation.location) + "\","
                + "\"category\":\"" + escapeJson(conversation.category) + "\","
                + "\"type\":\"" + escapeJson(conversation.type) + "\","
                + "\"studentEmail\":\"" + escapeJson(conversation.studentEmail) + "\","
                + "\"employerEmail\":\"" + escapeJson(conversation.employerEmail) + "\","
                + "\"otherEmail\":\"" + escapeJson(otherEmail) + "\","
                + "\"cvLink\":\"" + escapeJson(conversation.cvLink) + "\","
                + "\"appliedAt\":\"" + escapeJson(conversation.appliedAt) + "\","
                + "\"createdAt\":\"" + escapeJson(conversation.createdAt) + "\","
                + "\"lastMessageAt\":\"" + escapeJson(conversation.lastMessageAt) + "\""
                + "}";
    }

    private boolean hasLoggedInUser(HttpSession session) {
        return session != null && session.getAttribute("userEmail") != null;
    }

    private String normalizeAccountType(String value) {
        String cleanValue = clean(value);

        if ("Employer".equalsIgnoreCase(cleanValue)) {
            return "Employer";
        }

        if ("Student".equalsIgnoreCase(cleanValue)) {
            return "Student";
        }

        return "";
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String firstNonEmpty(String first,
                                 String second) {

        String cleanFirst = clean(first);
        return cleanFirst.isEmpty() ? clean(second) : cleanFirst;
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"loggedIn\":false,\"unreadCount\":0,\"conversations\":[]}"
        );
    }

    private void sendError(HttpServletResponse response,
                           int statusCode,
                           String message) throws IOException {

        response.setStatus(statusCode);
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private String escapeJson(String value) {
        return clean(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static class ApplicationSummary {
        private final int applicationId;
        private final int jobId;
        private final String employerEmail;
        private final String studentEmail;
        private final String jobTitle;
        private final String company;

        private ApplicationSummary(int applicationId,
                                   int jobId,
                                   String employerEmail,
                                   String studentEmail,
                                   String jobTitle,
                                   String company) {
            this.applicationId = applicationId;
            this.jobId = jobId;
            this.employerEmail = employerEmail;
            this.studentEmail = studentEmail;
            this.jobTitle = jobTitle;
            this.company = company;
        }
    }

    private static class ConversationSummary {
        private final int id;
        private final int applicationId;
        private final int jobId;
        private final String employerEmail;
        private final String studentEmail;
        private final String jobTitle;
        private final String company;
        private final String location;
        private final String category;
        private final String type;
        private final String cvLink;
        private final String appliedAt;
        private final String createdAt;
        private final String lastMessageAt;

        private ConversationSummary(int id,
                                    int applicationId,
                                    int jobId,
                                    String employerEmail,
                                    String studentEmail,
                                    String jobTitle,
                                    String company,
                                    String location,
                                    String category,
                                    String type,
                                    String cvLink,
                                    String appliedAt,
                                    String createdAt,
                                    String lastMessageAt) {
            this.id = id;
            this.applicationId = applicationId;
            this.jobId = jobId;
            this.employerEmail = employerEmail;
            this.studentEmail = studentEmail;
            this.jobTitle = jobTitle;
            this.company = company;
            this.location = location;
            this.category = category;
            this.type = type;
            this.cvLink = cvLink;
            this.appliedAt = appliedAt;
            this.createdAt = createdAt;
            this.lastMessageAt = lastMessageAt;
        }
    }
}
