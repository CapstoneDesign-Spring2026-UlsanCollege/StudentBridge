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

@WebServlet("/NotificationServlet")
public class NotificationServlet extends HttpServlet {

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
        String accountType = clean((String) session.getAttribute("accountType"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database connection failed");
                return;
            }

            DatabaseSchemaManager.ensureNotificationsTable(con);

            int unreadCount = countUnread(con, userEmail, accountType);
            String notificationsJson = listNotifications(con, userEmail, accountType);

            response.getWriter().write(
                    "{"
                            + "\"loggedIn\":true,"
                            + "\"unreadCount\":" + unreadCount + ","
                            + "\"notifications\":" + notificationsJson
                            + "}"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load notifications");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        prepareJsonResponse(response);

        HttpSession session = request.getSession(false);

        if (!hasLoggedInUser(session)) {
            sendUnauthorized(response);
            return;
        }

        String action = clean(request.getParameter("action"));
        String userEmail = clean((String) session.getAttribute("userEmail"));
        String accountType = clean((String) session.getAttribute("accountType"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Database connection failed");
                return;
            }

            DatabaseSchemaManager.ensureNotificationsTable(con);

            if ("read".equalsIgnoreCase(action)) {
                int notificationId = parseId(request.getParameter("id"));

                if (notificationId <= 0) {
                    sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                            "Notification id is required");
                    return;
                }

                markRead(con, userEmail, accountType, notificationId);
                response.getWriter().write("{\"success\":true}");
                return;
            }

            if ("readAll".equalsIgnoreCase(action)) {
                markAllRead(con, userEmail, accountType);
                response.getWriter().write("{\"success\":true}");
                return;
            }

            sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "Unsupported notification action");
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update notification");
        }
    }

    private int countUnread(Connection con,
                            String userEmail,
                            String accountType) throws SQLException {

        String sql = "SELECT COUNT(*) FROM notifications " +
                "WHERE recipient_email = ? AND recipient_account_type = ? AND is_read = FALSE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, accountType);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private String listNotifications(Connection con,
                                     String userEmail,
                                     String accountType) throws SQLException {

        String sql = "SELECT id, type, title, message, target_url, is_read, " +
                "related_job_id, related_application_id, created_at " +
                "FROM notifications " +
                "WHERE recipient_email = ? AND recipient_account_type = ? " +
                "ORDER BY created_at DESC, id DESC LIMIT 10";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, accountType);

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
                    json.append("\"type\":\"").append(escapeJson(rs.getString("type"))).append("\",");
                    json.append("\"title\":\"").append(escapeJson(rs.getString("title"))).append("\",");
                    json.append("\"message\":\"").append(escapeJson(rs.getString("message"))).append("\",");
                    json.append("\"targetUrl\":\"").append(escapeJson(rs.getString("target_url"))).append("\",");
                    json.append("\"read\":").append(rs.getBoolean("is_read")).append(",");
                    json.append("\"relatedJobId\":").append(intOrNull(rs.getString("related_job_id"))).append(",");
                    json.append("\"relatedApplicationId\":").append(intOrNull(rs.getString("related_application_id"))).append(",");
                    json.append("\"createdAt\":\"").append(escapeJson(rs.getString("created_at"))).append("\"");
                    json.append("}");

                    first = false;
                }

                json.append("]");
                return json.toString();
            }
        }
    }

    private void markRead(Connection con,
                          String userEmail,
                          String accountType,
                          int notificationId) throws SQLException {

        String sql = "UPDATE notifications SET is_read = TRUE, read_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND recipient_email = ? AND recipient_account_type = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setString(2, userEmail);
            ps.setString(3, accountType);
            ps.executeUpdate();
        }
    }

    private void markAllRead(Connection con,
                             String userEmail,
                             String accountType) throws SQLException {

        String sql = "UPDATE notifications SET is_read = TRUE, read_at = CURRENT_TIMESTAMP " +
                "WHERE recipient_email = ? AND recipient_account_type = ? AND is_read = FALSE";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, accountType);
            ps.executeUpdate();
        }
    }

    private boolean hasLoggedInUser(HttpSession session) {
        return session != null && session.getAttribute("userEmail") != null;
    }

    private int parseId(String value) {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            return -1;
        }
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

    private void sendUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(
                "{\"loggedIn\":false,\"unreadCount\":0,\"notifications\":[]}"
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
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
