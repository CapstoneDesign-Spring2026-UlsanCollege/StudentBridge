import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StudentLocationServlet")
public class StudentLocationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {
            response.sendRedirect("frontend/login.html?error=loginRequired");
            return;
        }

        String accountType = clean((String) session.getAttribute("accountType"));

        if (!accountType.isEmpty() && !"Student".equalsIgnoreCase(accountType)) {
            response.sendRedirect("frontend/student-profile.html?error=notStudent");
            return;
        }

        String userEmail = clean((String) session.getAttribute("userEmail"));
        String address = clean(request.getParameter("studentAddress"));
        BigDecimal latitude = parseDecimal(request.getParameter("studentLatitude"));
        BigDecimal longitude = parseDecimal(request.getParameter("studentLongitude"));

        if (address.isEmpty()) {
            response.sendRedirect("frontend/student-profile.html?error=missing");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.sendRedirect("frontend/student-profile.html?error=db");
                return;
            }

            saveStudentLocation(con, userEmail, address, latitude, longitude);
            response.sendRedirect("frontend/student-profile.html?success=1");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("frontend/student-profile.html?error=db");
        }
    }

    private void saveStudentLocation(Connection con, String userEmail, String address, BigDecimal latitude,
            BigDecimal longitude) throws SQLException {

        String updateSql = "UPDATE student_profiles "
                + "SET address = ?, latitude = ?, longitude = ? "
                + "WHERE user_email = ?";

        try (PreparedStatement ps = con.prepareStatement(updateSql)) {
            ps.setString(1, address);
            setNullableDecimal(ps, 2, latitude);
            setNullableDecimal(ps, 3, longitude);
            ps.setString(4, userEmail);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return;
            }
        }

        String insertSql = "INSERT INTO student_profiles (user_email, address, latitude, longitude) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setString(1, userEmail);
            ps.setString(2, address);
            setNullableDecimal(ps, 3, latitude);
            setNullableDecimal(ps, 4, longitude);
            ps.executeUpdate();
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private BigDecimal parseDecimal(String value) {
        String cleanedValue = clean(value);

        if (cleanedValue.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(cleanedValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void setNullableDecimal(PreparedStatement ps, int index, BigDecimal value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
            return;
        }

        ps.setBigDecimal(index, value);
    }
}
