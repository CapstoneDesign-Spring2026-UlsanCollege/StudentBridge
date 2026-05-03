import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/PostJobServlet")
public class PostJobServlet extends HttpServlet {

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

        if (!accountType.isEmpty() && !"Employer".equalsIgnoreCase(accountType)) {
            response.sendRedirect("frontend/post-job.html?error=notEmployer");
            return;
        }

        String title = clean(request.getParameter("title"));
        String company = clean(request.getParameter("company"));
        String location = clean(request.getParameter("location"));
        String category = clean(request.getParameter("category"));
        String type = clean(request.getParameter("type"));
        String salary = clean(request.getParameter("salary"));
        String description = clean(request.getParameter("description"));
        String workingHours = clean(request.getParameter("workingHours"));
        String requirements = clean(request.getParameter("requirements"));
        String contactEmail = clean(request.getParameter("contactEmail"));
        String employerEmail = clean((String) session.getAttribute("userEmail"));

        if (title.isEmpty() || company.isEmpty() || location.isEmpty() || category.isEmpty()
                || type.isEmpty() || salary.isEmpty() || description.isEmpty()) {
            response.sendRedirect("frontend/post-job.html?error=missing");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.sendRedirect("frontend/post-job.html?error=db");
                return;
            }

            insertJob(con, title, company, location, category, type, salary, description,
                    employerEmail, workingHours, requirements, contactEmail);

            response.sendRedirect("frontend/post-job.html?success=1");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("frontend/post-job.html?error=db");
        }
    }

    private void insertJob(Connection con, String title, String company, String location, String category,
            String type, String salary, String description, String employerEmail, String workingHours,
            String requirements, String contactEmail) throws SQLException {

        String extendedSql = "INSERT INTO jobs "
                + "(title, company, location, category, type, salary, description, employer_email, working_hours, requirements, contact_email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(extendedSql)) {
            ps.setString(1, title);
            ps.setString(2, company);
            ps.setString(3, location);
            ps.setString(4, category);
            ps.setString(5, type);
            ps.setString(6, salary);
            ps.setString(7, description);
            ps.setString(8, employerEmail);
            ps.setString(9, workingHours);
            ps.setString(10, requirements);
            ps.setString(11, contactEmail);
            ps.executeUpdate();
            return;
        } catch (SQLException e) {
            if (!isMissingOptionalJobColumn(e)) {
                throw e;
            }
        }

        // Backward-compatible insert for the original jobs table.
        String baseSql = "INSERT INTO jobs (title, company, location, category, type, salary, description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(baseSql)) {
            ps.setString(1, title);
            ps.setString(2, company);
            ps.setString(3, location);
            ps.setString(4, category);
            ps.setString(5, type);
            ps.setString(6, salary);
            ps.setString(7, description);
            ps.executeUpdate();
        }
    }

    private boolean isMissingOptionalJobColumn(SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        return "42S22".equals(e.getSQLState())
                || message.contains("employer_email")
                || message.contains("working_hours")
                || message.contains("requirements")
                || message.contains("contact_email");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
