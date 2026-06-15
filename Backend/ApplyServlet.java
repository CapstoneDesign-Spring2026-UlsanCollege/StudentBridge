import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ApplyServlet")
public class ApplyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        boolean jsonResponse = wantsJsonResponse(request);

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userEmail") == null) {
            sendApplyResponse(
                    response,
                    jsonResponse,
                    "loginRequired",
                    "frontend/login.html?error=loginRequired&source=apply",
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        String accountType = clean((String) session.getAttribute("accountType"));

        if (!"Student".equalsIgnoreCase(accountType)) {
            sendApplyResponse(
                    response,
                    jsonResponse,
                    "notStudent",
                    "frontend/jobsearch.html?apply=notStudent",
                    HttpServletResponse.SC_FORBIDDEN
            );
            return;
        }

        int jobId = parseJobId(request.getParameter("jobId"));

        if (jobId <= 0) {
            sendApplyResponse(
                    response,
                    jsonResponse,
                    "missingJob",
                    "frontend/jobsearch.html?apply=missingJob",
                    HttpServletResponse.SC_BAD_REQUEST
            );
            return;
        }

        String studentEmail = clean((String) session.getAttribute("userEmail"));
        int studentId = parseSessionInt(session.getAttribute("userId"));

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                sendApplyResponse(
                        response,
                        jsonResponse,
                        "database",
                        "frontend/jobsearch.html?apply=db",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
                return;
            }

            DatabaseSchemaManager.ensureApplicationsTable(con);

            JobSummary job = getJobSummary(con, jobId);

            if (job == null) {
                sendApplyResponse(
                        response,
                        jsonResponse,
                        "missingJob",
                        "frontend/jobsearch.html?apply=missingJob",
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            if (alreadyApplied(con, jobId, studentEmail)) {
                sendApplyResponse(
                        response,
                        jsonResponse,
                        "duplicate",
                        "frontend/jobsearch.html?apply=duplicate",
                        HttpServletResponse.SC_CONFLICT
                );
                return;
            }

            String cvLink = getStudentCvLink(con, studentEmail);
            int applicationId = saveApplication(con, jobId, studentId, studentEmail, cvLink);
            createApplicationNotifications(con, job, applicationId, studentEmail);
            sendApplyResponse(
                    response,
                    jsonResponse,
                    "success",
                    "frontend/jobsearch.html?apply=success",
                    HttpServletResponse.SC_OK
            );

        } catch (SQLException e) {
            e.printStackTrace();

            if (isDuplicateApplication(e)) {
                sendApplyResponse(
                        response,
                        jsonResponse,
                        "duplicate",
                        "frontend/jobsearch.html?apply=duplicate",
                        HttpServletResponse.SC_CONFLICT
                );
            } else {
                sendApplyResponse(
                        response,
                        jsonResponse,
                        "database",
                        "frontend/jobsearch.html?apply=database",
                        HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                );
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendRedirect("frontend/jobsearch.html");
    }

    private JobSummary getJobSummary(Connection con,
                                     int jobId) throws SQLException {

        String sql = "SELECT id, title, company, employer_email FROM jobs WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new JobSummary(
                            rs.getInt("id"),
                            clean(rs.getString("title")),
                            clean(rs.getString("company")),
                            clean(rs.getString("employer_email"))
                    );
                }
            }
        }

        return null;
    }

    private String getStudentCvLink(Connection con,
                                    String studentEmail) throws SQLException {

        String sql = "SELECT cv_link FROM student_profiles " +
                "WHERE user_email = ? ORDER BY id DESC LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return clean(rs.getString("cv_link"));
                }
            }
        }

        return "";
    }

    private boolean alreadyApplied(Connection con,
                                   int jobId,
                                   String studentEmail) throws SQLException {

        String sql = "SELECT id FROM applications WHERE job_id = ? AND student_email = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, jobId);
            ps.setString(2, studentEmail);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int saveApplication(Connection con,
                                int jobId,
                                int studentId,
                                String studentEmail,
                                String cvLink) throws SQLException {

        String sql = "INSERT INTO applications (job_id, student_id, student_email, cv_link) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jobId);
            if (studentId > 0) {
                ps.setInt(2, studentId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, studentEmail);
            ps.setString(4, cvLink);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return -1;
    }

    private void createApplicationNotifications(Connection con,
                                                JobSummary job,
                                                int applicationId,
                                                String studentEmail) {

        try {
            NotificationService.createNotification(
                    con,
                    studentEmail,
                    "Student",
                    "application_sent",
                    "Application sent",
                    "Application submitted successfully for " + job.title + ".",
                    "/frontend/jobsearch.html?apply=success",
                    job.id,
                    applicationId
            );

            if (!job.employerEmail.isEmpty()) {
                NotificationService.createNotification(
                        con,
                        job.employerEmail,
                        "Employer",
                        "new_application",
                        "New job application",
                        studentEmail + " applied to " + job.title + ".",
                        "/frontend/employer-dashboard.html",
                        job.id,
                        applicationId
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int parseJobId(String value) {
        try {
            return Integer.parseInt(clean(value));
        } catch (NumberFormatException e) {
            return -1;
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

    private boolean isDuplicateApplication(SQLException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        return "23000".equals(e.getSQLState()) && message.contains("duplicate");
    }

    private boolean wantsJsonResponse(HttpServletRequest request) {
        String requestedWith = clean(request.getHeader("X-Requested-With"));
        String accept = clean(request.getHeader("Accept")).toLowerCase();
        String format = clean(request.getParameter("format"));

        return "XMLHttpRequest".equalsIgnoreCase(requestedWith)
                || accept.contains("application/json")
                || "json".equalsIgnoreCase(format);
    }

    private void sendApplyResponse(HttpServletResponse response,
                                   boolean jsonResponse,
                                   String status,
                                   String redirectUrl,
                                   int statusCode) throws IOException {

        if (!jsonResponse) {
            response.sendRedirect(redirectUrl);
            return;
        }

        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"status\":\"" + escapeJson(status) + "\","
                        + "\"message\":\"" + escapeJson(messageForStatus(status)) + "\","
                        + "\"redirect\":\"" + escapeJson(redirectUrl) + "\"}"
        );
    }

    private String messageForStatus(String status) {
        if ("success".equals(status)) {
            return "Application submitted successfully.";
        }

        if ("duplicate".equals(status)) {
            return "You already applied for this job.";
        }

        if ("loginRequired".equals(status)) {
            return "Please log in as a student to apply.";
        }

        if ("notStudent".equals(status)) {
            return "Only student accounts can apply for jobs.";
        }

        return "Unable to submit application.";
    }

    private String escapeJson(String value) {
        return clean(value)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private static class JobSummary {
        private final int id;
        private final String title;
        private final String company;
        private final String employerEmail;

        private JobSummary(int id,
                           String title,
                           String company,
                           String employerEmail) {
            this.id = id;
            this.title = title.isEmpty() ? company : title;
            this.company = company;
            this.employerEmail = employerEmail;
        }
    }
}
