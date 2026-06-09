import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/EmployerApplicationsServlet")
public class EmployerApplicationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        HttpSession session = request.getSession(false);

        if (!hasLoggedInUser(session)) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Login required");
            return;
        }

        String accountType = clean((String) session.getAttribute("accountType"));

        if (!"Employer".equalsIgnoreCase(accountType)) {
            sendError(response, HttpServletResponse.SC_FORBIDDEN,
                    "Employer account required");
            return;
        }

        String employerEmail = clean((String) session.getAttribute("userEmail"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database connection failed");
                return;
            }

            DatabaseSchemaManager.ensureJobsTable(con);
            DatabaseSchemaManager.ensureMessagingTables(con);

            response.getWriter().write(
                    "{\"loggedIn\":true,\"applications\":"
                            + listApplications(con, employerEmail)
                            + "}"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load applications");
        }
    }

    private String listApplications(Connection con,
                                    String employerEmail) throws SQLException {

        String sql = "SELECT a.id AS application_id, a.job_id, a.student_email, " +
                "a.cv_link, a.created_at AS applied_at, j.title, j.company, " +
                "j.location, j.category, j.type, c.id AS conversation_id, " +
                "(SELECT COUNT(*) FROM messages m " +
                "WHERE m.conversation_id = c.id AND m.sender_email <> ? " +
                "AND m.read_at IS NULL) AS unread_count " +
                "FROM applications a " +
                "JOIN jobs j ON j.id = a.job_id " +
                "LEFT JOIN conversations c ON c.application_id = a.id " +
                "WHERE j.employer_email = ? " +
                "ORDER BY a.created_at DESC, a.id DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, employerEmail);
            ps.setString(2, employerEmail);

            try (ResultSet rs = ps.executeQuery()) {
                StringBuilder json = new StringBuilder();
                json.append("[");

                boolean first = true;

                while (rs.next()) {
                    if (!first) {
                        json.append(",");
                    }

                    json.append("{");
                    json.append("\"applicationId\":").append(rs.getInt("application_id")).append(",");
                    json.append("\"jobId\":").append(rs.getInt("job_id")).append(",");
                    json.append("\"jobTitle\":\"").append(escapeJson(rs.getString("title"))).append("\",");
                    json.append("\"company\":\"").append(escapeJson(rs.getString("company"))).append("\",");
                    json.append("\"location\":\"").append(escapeJson(rs.getString("location"))).append("\",");
                    json.append("\"category\":\"").append(escapeJson(rs.getString("category"))).append("\",");
                    json.append("\"type\":\"").append(escapeJson(rs.getString("type"))).append("\",");
                    json.append("\"studentEmail\":\"").append(escapeJson(rs.getString("student_email"))).append("\",");
                    json.append("\"cvLink\":\"").append(escapeJson(rs.getString("cv_link"))).append("\",");
                    json.append("\"appliedAt\":\"").append(escapeJson(rs.getString("applied_at"))).append("\",");
                    json.append("\"conversationId\":").append(intOrNull(rs.getString("conversation_id"))).append(",");
                    json.append("\"unreadCount\":").append(rs.getInt("unread_count"));
                    json.append("}");

                    first = false;
                }

                json.append("]");
                return json.toString();
            }
        }
    }

    private boolean hasLoggedInUser(HttpSession session) {
        return session != null && session.getAttribute("userEmail") != null;
    }

    private String intOrNull(String value) {
        String cleanValue = clean(value);

        if (cleanValue.isEmpty()) {
            return "null";
        }

        try {
            Integer.parseInt(cleanValue);
            return cleanValue;
        } catch (NumberFormatException e) {
            return "null";
        }
    }

    private void prepareJsonResponse(HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
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
}
