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

@WebServlet("/JobServlet")
public class JobServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        String language = TranslationService.normalizeLanguage(request.getParameter("lang"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection failed.");
                return;
            }

            try {
                writeJobsJson(con, response, true, language);
            } catch (SQLException e) {
                if (!isMissingLocationColumn(e)) {
                    throw e;
                }

                writeJobsJson(con, response, false, language);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to load jobs.");
        }
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write("{\"error\":\"" + escapeJson(message) + "\"}");
    }

    private void writeJobsJson(Connection con, HttpServletResponse response, boolean includeMapColumns, String language)
            throws SQLException, IOException {

        String sql = includeMapColumns
                ? "SELECT id, title, company, location, category, type, salary, description, address, latitude, longitude FROM jobs ORDER BY id DESC"
                : "SELECT id, title, company, location, category, type, salary, description FROM jobs ORDER BY id DESC";
        TranslationService translationService = new TranslationService();

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            StringBuilder json = new StringBuilder();
            json.append("[");

            boolean first = true;

            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }

                String title = rs.getString("title");
                String location = rs.getString("location");
                String category = rs.getString("category");
                String type = rs.getString("type");
                String description = rs.getString("description");
                String address = includeMapColumns ? rs.getString("address") : "";

                String translatedTitle = translationService.translate(con, title, language);
                String translatedLocation = translationService.translate(con, location, language);
                String translatedCategory = translationService.translate(con, category, language);
                String translatedType = translationService.translate(con, type, language);
                String translatedDescription = translationService.translate(con, description, language);
                String translatedAddress = translationService.translate(con, address, language);

                json.append("{")
                        .append("\"id\":").append(rs.getInt("id")).append(",")
                        .append("\"title\":\"").append(escapeJson(translatedTitle)).append("\",")
                        .append("\"originalTitle\":\"").append(escapeJson(title)).append("\",")
                        .append("\"company\":\"").append(escapeJson(rs.getString("company"))).append("\",")
                        .append("\"location\":\"").append(escapeJson(translatedLocation)).append("\",")
                        .append("\"originalLocation\":\"").append(escapeJson(location)).append("\",")
                        .append("\"category\":\"").append(escapeJson(translatedCategory)).append("\",")
                        .append("\"originalCategory\":\"").append(escapeJson(category)).append("\",")
                        .append("\"type\":\"").append(escapeJson(translatedType)).append("\",")
                        .append("\"originalType\":\"").append(escapeJson(type)).append("\",")
                        .append("\"salary\":\"").append(escapeJson(rs.getString("salary"))).append("\",")
                        .append("\"description\":\"").append(escapeJson(translatedDescription)).append("\",")
                        .append("\"originalDescription\":\"").append(escapeJson(description)).append("\",")
                        .append("\"address\":\"").append(escapeJson(translatedAddress)).append("\",")
                        .append("\"originalAddress\":\"").append(escapeJson(address)).append("\",")
                        .append("\"latitude\":").append(decimalOrNull(includeMapColumns ? rs.getString("latitude") : null)).append(",")
                        .append("\"longitude\":").append(decimalOrNull(includeMapColumns ? rs.getString("longitude") : null))
                        .append("}");

                first = false;
            }

            json.append("]");
            response.getWriter().write(json.toString());
        }
    }

    private String decimalOrNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "null";
        }

        try {
            Double.parseDouble(value);
            return value;
        } catch (NumberFormatException e) {
            return "null";
        }
    }

    private boolean isMissingLocationColumn(SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        return "42S22".equals(e.getSQLState())
                || message.contains("address")
                || message.contains("latitude")
                || message.contains("longitude");
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            switch (current) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (current < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) current));
                    } else {
                        escaped.append(current);
                    }
                    break;
            }
        }

        return escaped.toString();
    }
}
