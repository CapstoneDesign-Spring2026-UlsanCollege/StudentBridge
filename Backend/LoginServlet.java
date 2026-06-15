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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Set encoding
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // Validation
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            redirectToLogin(req, resp, "empty");
            return;
        }

        // Trim spaces
        email = email.trim();
        password = password.trim();

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection()) {

            // Check database connection
            if (con == null) {

                redirectToLogin(req, resp, "database");
                return;
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                // Set query parameters
                ps.setString(1, email);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {

                    // Login successful
                    if (rs.next()) {

                        HttpSession session = req.getSession();

                        String userName = safeGetString(rs, "name");
                        String userEmail = safeGetString(rs, "email");
                        String accountType = getAccountType(rs);
                        int userId = safeGetInt(rs, "id");

                        session.setAttribute("user", userName.isEmpty() ? userEmail : userName);
                        session.setAttribute("userId", userId);
                        session.setAttribute("userEmail", userEmail);
                        session.setAttribute("accountType", accountType);

                        // Session timeout (30 minutes)
                        session.setMaxInactiveInterval(30 * 60);

                        resp.sendRedirect(req.getContextPath() + "/index.html?login=success");

                    } else {

                        // Invalid login
                        redirectToLogin(req, resp, "invalid");
                    }
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();

            redirectToLogin(req, resp, "database");

        } catch (Exception e) {

            e.printStackTrace();

            redirectToLogin(req, resp, "server");
        }
    }

    private void redirectToLogin(HttpServletRequest req,
                                 HttpServletResponse resp,
                                 String error)
            throws IOException {

        resp.sendRedirect(req.getContextPath()
                + "/frontend/login.html?error=" + error);
    }

    private String getAccountType(ResultSet rs) {

        String accountType = safeGetString(rs, "account_type");

        if (accountType.isEmpty()) {
            accountType = safeGetString(rs, "accountType");
        }

        if (accountType.isEmpty()) {
            accountType = "Student";
        }

        return accountType;
    }

    // Safe column getter
    private String safeGetString(ResultSet rs, String columnName) {

        try {

            String value = rs.getString(columnName);

            return value == null ? "" : value;

        } catch (SQLException e) {

            return "";
        }
    }

    private int safeGetInt(ResultSet rs, String columnName) {
        try {
            return rs.getInt(columnName);
        } catch (SQLException e) {
            return -1;
        }
    }

}
