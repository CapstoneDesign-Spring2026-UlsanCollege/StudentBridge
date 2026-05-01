import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        String accountType = clean(request.getParameter("accountType"));
        String password = clean(request.getParameter("password"));
        String confirmPassword = clean(request.getParameter("confirmPassword"));

        if (accountType.isEmpty()) {
            response.sendRedirect("frontend/register.html?error=accountType");
            return;
        }

        if (password.isEmpty() || !password.equals(confirmPassword)) {
            response.sendRedirect("frontend/register.html?error=password");
            return;
        }

        String name = getDisplayName(request, accountType);
        String email = getEmail(request, accountType);
        String phone = getPhone(request, accountType);

        if (email.isEmpty()) {
            response.sendRedirect("frontend/register.html?error=email");
            return;
        }

        Connection con = null;

        try {
            con = DBConnection.getConnection();

            if (con == null) {
                response.getWriter().println("Database connection failed.");
                return;
            }

            con.setAutoCommit(false);

            insertUser(con, name, email, phone, password, accountType);

            if ("Student".equalsIgnoreCase(accountType)) {
                tryInsertStudentProfile(con, request, email);
            } else if ("Employer".equalsIgnoreCase(accountType)) {
                tryInsertEmployerProfile(con, request, email);
            }

            con.commit();
            response.sendRedirect("frontend/login.html?registered=1");

        } catch (Exception e) {
            rollbackQuietly(con);
            e.printStackTrace();
            response.getWriter().println("Registration error: " + e.getMessage());
        } finally {
            closeQuietly(con);
        }
    }

    private void insertUser(Connection con, String name, String email, String phone, String password, String accountType)
            throws SQLException {

        String sqlWithAccountType = "INSERT INTO users (name, email, phone, password, account_type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sqlWithAccountType)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);
            ps.setString(5, accountType);
            ps.executeUpdate();
            return;
        } catch (SQLException e) {
            if (!isMissingAccountTypeColumn(e)) {
                throw e;
            }
        }

        // Backward-compatible fallback for the original users table.
        String oldSql = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(oldSql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);
            ps.executeUpdate();
        }
    }

    private void tryInsertStudentProfile(Connection con, HttpServletRequest request, String email) {
        String sql = "INSERT INTO student_profiles "
                + "(user_email, university_name, major, student_id, preferred_job_category, available_working_time, korean_language_level) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, clean(request.getParameter("universityName")));
            ps.setString(3, clean(request.getParameter("major")));
            ps.setString(4, clean(request.getParameter("studentId")));
            ps.setString(5, clean(request.getParameter("preferredJobCategory")));
            ps.setString(6, clean(request.getParameter("availableWorkingTime")));
            ps.setString(7, clean(request.getParameter("koreanLanguageLevel")));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Student profile table not ready yet. User registration still completed.");
            e.printStackTrace();
        }
    }

    private void tryInsertEmployerProfile(Connection con, HttpServletRequest request, String email) {
        String sql = "INSERT INTO employer_profiles "
                + "(user_email, business_name, manager_name, business_location, business_type, job_posting_category, company_registration_number, company_description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, clean(request.getParameter("businessName")));
            ps.setString(3, clean(request.getParameter("managerName")));
            ps.setString(4, clean(request.getParameter("businessLocation")));
            ps.setString(5, clean(request.getParameter("businessType")));
            ps.setString(6, clean(request.getParameter("jobPostingCategory")));
            ps.setString(7, clean(request.getParameter("companyRegistrationNumber")));
            ps.setString(8, clean(request.getParameter("companyDescription")));
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Employer profile table not ready yet. User registration still completed.");
            e.printStackTrace();
        }
    }

    private String getDisplayName(HttpServletRequest request, String accountType) {
        if ("Employer".equalsIgnoreCase(accountType)) {
            String businessName = clean(request.getParameter("businessName"));

            if (!businessName.isEmpty()) {
                return businessName;
            }

            String managerName = clean(request.getParameter("managerName"));

            if (!managerName.isEmpty()) {
                return managerName;
            }
        }

        return clean(request.getParameter("name"));
    }

    private String getEmail(HttpServletRequest request, String accountType) {
        if ("Employer".equalsIgnoreCase(accountType)) {
            String businessEmail = clean(request.getParameter("businessEmail"));

            if (!businessEmail.isEmpty()) {
                return businessEmail;
            }
        }

        return clean(request.getParameter("email"));
    }

    private String getPhone(HttpServletRequest request, String accountType) {
        if ("Employer".equalsIgnoreCase(accountType)) {
            String businessPhone = clean(request.getParameter("businessPhone"));

            if (!businessPhone.isEmpty()) {
                return businessPhone;
            }
        }

        return clean(request.getParameter("phone"));
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isMissingAccountTypeColumn(SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        return "42S22".equals(e.getSQLState()) || message.contains("account_type");
    }

    private void rollbackQuietly(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void closeQuietly(Connection con) {
        if (con == null) {
            return;
        }

        try {
            con.close();
        } catch (SQLException ignored) {
        }
    }
}
