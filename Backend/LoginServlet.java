import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

            resp.sendRedirect("frontend/login.html?error=empty");
            return;
        }

        // Trim spaces
        email = email.trim();
        password = password.trim();

        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)
        ) {

            // Check database connection
            if (con == null) {

                resp.getWriter().println("Database connection failed.");
                return;
            }

            // Set query parameters
            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                // Login successful
                if (rs.next()) {

                    HttpSession session = req.getSession();

                    session.setAttribute("user", safeGetString(rs, "name"));
                    session.setAttribute("userEmail", safeGetString(rs, "email"));
                    session.setAttribute("accountType", safeGetString(rs, "account_type"));

                    // Session timeout (30 minutes)
                    session.setMaxInactiveInterval(30 * 60);

                    String encodedEmail = URLEncoder.encode(
                            safeGetString(rs, "email"),
                            StandardCharsets.UTF_8
                    );

                    resp.sendRedirect(
                            "index.html?login=success&email=" + encodedEmail
                    );

                } else {

                    // Invalid login
                    resp.sendRedirect("frontend/login.html?error=invalid");
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();

            resp.sendRedirect("frontend/login.html?error=database");

        } catch (Exception e) {

            e.printStackTrace();

            resp.sendRedirect("frontend/login.html?error=server");
        }
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
}
