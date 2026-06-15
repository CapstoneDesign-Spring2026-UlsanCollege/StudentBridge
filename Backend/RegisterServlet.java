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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String accountType = clean(
                request.getParameter("accountType")
        );

        String password = clean(
                request.getParameter("password")
        );

        String confirmPassword = clean(
                request.getParameter("confirmPassword")
        );

        // Validate account type
        if (accountType.isEmpty()
                || (!"Student".equalsIgnoreCase(accountType)
                && !"Employer".equalsIgnoreCase(accountType))) {

            response.sendRedirect(
                    "frontend/register.html?error=accountType"
            );

            return;
        }

        // Validate password
        if (password.isEmpty()
                || !password.equals(confirmPassword)) {

            response.sendRedirect(
                    "frontend/register.html?error=password"
            );

            return;
        }

        String name = getDisplayName(request, accountType);
        String email = getEmail(request, accountType);
        String phone = getPhone(request, accountType);

        // Email validation
        if (email.isEmpty()) {

            response.sendRedirect(
                    "frontend/register.html?error=email"
            );

            return;
        }

        try (Connection con = DBConnection.getConnection()) {

            if (con == null) {

                response.sendRedirect(
                        "frontend/register.html?error=db"
                );

                return;
            }

            con.setAutoCommit(false);

            // Check duplicate email
            if (isEmailExists(con, email)) {

                response.sendRedirect(
                        "frontend/register.html?error=emailExists"
                );

                return;
            }

            // Insert main user
            int userId = insertUser(
                    con,
                    name,
                    email,
                    phone,
                    password,
                    accountType
            );

            // Insert profiles
            if ("Student".equalsIgnoreCase(accountType)) {

                tryInsertStudentProfile(
                        con,
                        request,
                        email
                );

            } else if ("Employer".equalsIgnoreCase(accountType)) {

                tryInsertEmployerProfile(
                        con,
                        request,
                        email
                );
            }

            con.commit();

            HttpSession session = request.getSession(true);
            session.setAttribute("user", name);
            session.setAttribute("userId", userId);
            session.setAttribute("userEmail", email);
            session.setAttribute("accountType", accountType);
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect(
                    request.getContextPath() + "/index.html?login=success&registered=1"
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendRedirect(
                    "frontend/register.html?error=server"
            );
        }
    }

    private int insertUser(
            Connection con,
            String name,
            String email,
            String phone,
            String password,
            String accountType
    ) throws SQLException {

        String sqlWithAccountType =
                "INSERT INTO users " +
                "(name, email, phone, password, account_type) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(sqlWithAccountType, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);
            ps.setString(5, accountType);

            ps.executeUpdate();

            return getGeneratedId(ps, email, con);

        } catch (SQLException e) {

            if (!isMissingAccountTypeColumn(e)) {
                throw e;
            }
        }

        // Backward compatibility
        String oldSql =
                "INSERT INTO users " +
                "(name, email, phone, password) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps =
                     con.prepareStatement(oldSql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, password);

            ps.executeUpdate();
            return getGeneratedId(ps, email, con);
        }
    }

    private int getGeneratedId(PreparedStatement ps,
                               String email,
                               Connection con) throws SQLException {
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        String sql = "SELECT id FROM users WHERE email = ?";

        try (PreparedStatement lookup = con.prepareStatement(sql)) {
            lookup.setString(1, email);

            try (ResultSet rs = lookup.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        return -1;
    }

    private boolean isEmailExists(Connection con,
                                  String email)
            throws SQLException {

        String sql =
                "SELECT email FROM users WHERE email = ?";

        try (PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {

                return rs.next();
            }
        }
    }

    private void tryInsertStudentProfile(
            Connection con,
            HttpServletRequest request,
            String email
    ) {

        String sql =
                "INSERT INTO student_profiles " +
                "(user_email, university_name, major, student_id, " +
                "preferred_job_category, available_working_time, " +
                "korean_language_level, cv_link) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            DatabaseSchemaManager.ensureStudentProfilesCvColumn(con);

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, email);
                ps.setString(2,
                        clean(request.getParameter("universityName")));

                ps.setString(3,
                        clean(request.getParameter("major")));

                ps.setString(4,
                        clean(request.getParameter("studentId")));

                ps.setString(5,
                        clean(request.getParameter("preferredJobCategory")));

                ps.setString(6,
                        clean(request.getParameter("availableWorkingTime")));

                ps.setString(7,
                        clean(request.getParameter("koreanLanguageLevel")));

                ps.setString(8,
                        clean(request.getParameter("cvLink")));

                ps.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println(
                    "Student profile table not ready."
            );

            e.printStackTrace();
        }
    }

    private void tryInsertEmployerProfile(
            Connection con,
            HttpServletRequest request,
            String email
    ) {

        String sql =
                "INSERT INTO employer_profiles " +
                "(user_email, business_name, manager_name, " +
                "business_location, business_type, job_posting_category, " +
                "company_registration_number, company_description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            DatabaseSchemaManager.ensureEmployerProfilesTable(con);

            try (PreparedStatement ps =
                         con.prepareStatement(sql)) {

                ps.setString(1, email);

                ps.setString(2,
                        clean(request.getParameter("businessName")));

                ps.setString(3,
                        clean(request.getParameter("managerName")));

                ps.setString(4,
                        clean(request.getParameter("businessLocation")));

                ps.setString(5,
                        clean(request.getParameter("businessType")));

                ps.setString(6,
                        clean(request.getParameter("jobPostingCategory")));

                ps.setString(7,
                        clean(request.getParameter("companyRegistrationNumber")));

                ps.setString(8,
                        clean(request.getParameter("companyDescription")));

                ps.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println(
                    "Employer profile table not ready."
            );

            e.printStackTrace();
        }
    }

    private String getDisplayName(
            HttpServletRequest request,
            String accountType
    ) {

        if ("Employer".equalsIgnoreCase(accountType)) {

            String businessName =
                    clean(request.getParameter("businessName"));

            if (!businessName.isEmpty()) {
                return businessName;
            }

            String managerName =
                    clean(request.getParameter("managerName"));

            if (!managerName.isEmpty()) {
                return managerName;
            }
        }

        return clean(request.getParameter("name"));
    }

    private String getEmail(
            HttpServletRequest request,
            String accountType
    ) {

        if ("Employer".equalsIgnoreCase(accountType)) {

            String businessEmail =
                    clean(request.getParameter("businessEmail"));

            if (!businessEmail.isEmpty()) {
                return businessEmail;
            }
        }

        return clean(request.getParameter("email"));
    }

    private String getPhone(
            HttpServletRequest request,
            String accountType
    ) {

        if ("Employer".equalsIgnoreCase(accountType)) {

            String businessPhone =
                    clean(request.getParameter("businessPhone"));

            if (!businessPhone.isEmpty()) {
                return businessPhone;
            }
        }

        return clean(request.getParameter("phone"));
    }

    private String clean(String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private boolean isMissingAccountTypeColumn(SQLException e) {

        String message =
                e.getMessage() == null
                        ? ""
                        : e.getMessage().toLowerCase();

        return "42S22".equals(e.getSQLState())
                || message.contains("account_type");
    }

}
