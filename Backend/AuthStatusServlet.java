import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AuthStatusServlet")
public class AuthStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {
            response.getWriter().write("{\"loggedIn\":false}");
            return;
        }

        String name = safeString(session.getAttribute("user"));
        String userId = safeString(session.getAttribute("userId"));
        String email = safeString(session.getAttribute("userEmail"));
        String accountType = safeString(session.getAttribute("accountType"));

        response.getWriter().write(
                "{"
                        + "\"loggedIn\":true,"
                        + "\"name\":\"" + escapeJson(name) + "\","
                        + "\"userId\":\"" + escapeJson(userId) + "\","
                        + "\"email\":\"" + escapeJson(email) + "\","
                        + "\"accountType\":\"" + escapeJson(accountType) + "\""
                        + "}"
        );
    }

    private String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
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
