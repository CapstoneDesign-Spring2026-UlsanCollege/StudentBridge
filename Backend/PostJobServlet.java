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

@WebServlet("/PostJobServlet")
public class PostJobServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        // UTF-8 support
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // Check session
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {

            response.sendRedirect(
                    "frontend/login.html?error=loginRequired"
            );

            return;
        }

        // Verify employer account
        String accountType = clean(
                (String) session.getAttribute("accountType")
        );

        if (!"Employer".equalsIgnoreCase(accountType)) {

            response.sendRedirect(
                    "frontend/post-job.html?error=notEmployer"
            );

            return;
        }

        // Form data
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

        String address = clean(request.getParameter("address"));

        BigDecimal latitude = parseDecimal(
                request.getParameter("latitude")
        );

        BigDecimal longitude = parseDecimal(
                request.getParameter("longitude")
        );

        String employerEmail = clean(
                (String) session.getAttribute("userEmail")
        );
        int employerId = parseSessionInt(session.getAttribute("userId"));

        // Required field validation
        if (title.isEmpty()
                || company.isEmpty()
                || location.isEmpty()
                || category.isEmpty()
                || type.isEmpty()
                || salary.isEmpty()
                || description.isEmpty()
                || address.isEmpty()) {

            response.sendRedirect(
                    "frontend/post-job.html?error=missing"
            );

            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                response.sendRedirect(
                        "frontend/post-job.html?error=db"
                );

                return;
            }

            DatabaseSchemaManager.ensureJobsTable(con);

            insertJob(
                    con,
                    title,
                    company,
                    location,
                    category,
                    type,
                    salary,
                    description,
                    employerId,
                    employerEmail,
                    workingHours,
                    requirements,
                    contactEmail,
                    address,
                    latitude,
                    longitude
            );

            response.sendRedirect(
                    "frontend/post-job.html?success=1"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            response.sendRedirect(
                    "frontend/post-job.html?error=database"
            );
        }
    }

    private void insertJob(
            Connection con,
            String title,
            String company,
            String location,
            String category,
            String type,
            String salary,
            String description,
            int employerId,
            String employerEmail,
            String workingHours,
            String requirements,
            String contactEmail,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ) throws SQLException {

        String extendedSql =
                "INSERT INTO jobs " +
                "(title, company, location, category, type, salary, " +
                "description, employer_id, employer_email, working_hours, requirements, " +
                "contact_email, address, latitude, longitude) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(extendedSql)) {

            ps.setString(1, title);
            ps.setString(2, company);
            ps.setString(3, location);
            ps.setString(4, category);
            ps.setString(5, type);
            ps.setString(6, salary);
            ps.setString(7, description);

            if (employerId > 0) {
                ps.setInt(8, employerId);
            } else {
                ps.setNull(8, Types.INTEGER);
            }
            ps.setString(9, employerEmail);
            ps.setString(10, workingHours);
            ps.setString(11, requirements);
            ps.setString(12, contactEmail);

            ps.setString(13, address);

            setNullableDecimal(ps, 14, latitude);
            setNullableDecimal(ps, 15, longitude);

            ps.executeUpdate();

            return;

        } catch (SQLException e) {

            // Use old query if columns do not exist
            if (!isMissingOptionalJobColumn(e)) {
                throw e;
            }
        }

        // Backward compatible insert
        String baseSql =
                "INSERT INTO jobs " +
                "(title, company, location, category, type, salary, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(baseSql)) {

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

        String message = e.getMessage() == null
                ? ""
                : e.getMessage().toLowerCase();

        return "42S22".equals(e.getSQLState())
                || message.contains("employer_id")
                || message.contains("employer_email")
                || message.contains("working_hours")
                || message.contains("requirements")
                || message.contains("contact_email")
                || message.contains("address")
                || message.contains("latitude")
                || message.contains("longitude");
    }

    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
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

    private int parseSessionInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        try {
            return Integer.parseInt(clean(value == null ? "" : String.valueOf(value)));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void setNullableDecimal(
            PreparedStatement ps,
            int index,
            BigDecimal value
    ) throws SQLException {

        if (value == null) {

            ps.setNull(index, Types.DECIMAL);

        } else {

            ps.setBigDecimal(index, value);
        }
    }
}
