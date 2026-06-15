import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchemaManager {

    private DatabaseSchemaManager() {
    }

    public static void ensureStudentProfilesTable(Connection con) throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS student_profiles (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_email VARCHAR(255) NOT NULL, " +
                            "university_name VARCHAR(255), " +
                            "major VARCHAR(255), " +
                            "student_id VARCHAR(100), " +
                            "preferred_job_category VARCHAR(100), " +
                            "available_working_time VARCHAR(100), " +
                            "korean_language_level VARCHAR(100), " +
                            "cv_link VARCHAR(500), " +
                            "address VARCHAR(255), " +
                            "latitude DECIMAL(10, 7), " +
                            "longitude DECIMAL(10, 7), " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "INDEX idx_student_profiles_coordinates (latitude, longitude), " +
                            "INDEX idx_student_profiles_user_email (user_email)" +
                            ")"
            );
        }

        ensureColumn(con, "student_profiles", "cv_link", "VARCHAR(500)");
        ensureColumn(con, "student_profiles", "address", "VARCHAR(255)");
        ensureColumn(con, "student_profiles", "latitude", "DECIMAL(10, 7)");
        ensureColumn(con, "student_profiles", "longitude", "DECIMAL(10, 7)");
        ensureIndex(con, "student_profiles", "idx_student_profiles_user_email", "user_email");
        ensureIndex(con, "student_profiles", "idx_student_profiles_coordinates", "latitude, longitude");
    }

    public static void ensureEmployerProfilesTable(Connection con) throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS employer_profiles (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "user_email VARCHAR(255) NOT NULL, " +
                            "business_name VARCHAR(255), " +
                            "manager_name VARCHAR(255), " +
                            "business_location VARCHAR(255), " +
                            "business_type VARCHAR(100), " +
                            "job_posting_category VARCHAR(100), " +
                            "company_registration_number VARCHAR(100), " +
                            "company_description TEXT, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "INDEX idx_employer_profiles_user_email (user_email)" +
                            ")"
            );
        }

        ensureIndex(con, "employer_profiles", "idx_employer_profiles_user_email", "user_email");
    }

    public static void ensureJobsTable(Connection con) throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS jobs (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "title VARCHAR(150) NOT NULL, " +
                            "company VARCHAR(150) NOT NULL, " +
                            "location VARCHAR(100) NOT NULL, " +
                            "category VARCHAR(100) NOT NULL, " +
                            "type VARCHAR(50) NOT NULL, " +
                            "salary VARCHAR(100) NOT NULL, " +
                            "description TEXT NOT NULL, " +
                            "employer_id INT, " +
                            "employer_email VARCHAR(255), " +
                            "working_hours VARCHAR(150), " +
                            "requirements TEXT, " +
                            "contact_email VARCHAR(255), " +
                            "contact_phone VARCHAR(50), " +
                            "company_details TEXT, " +
                            "application_deadline DATE, " +
                            "logo_url VARCHAR(500), " +
                            "address VARCHAR(255), " +
                            "latitude DECIMAL(10, 7), " +
                            "longitude DECIMAL(10, 7), " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );
        }

        ensureColumn(con, "employer_id", "INT");
        ensureColumn(con, "employer_email", "VARCHAR(255)");
        ensureColumn(con, "working_hours", "VARCHAR(150)");
        ensureColumn(con, "requirements", "TEXT");
        ensureColumn(con, "contact_email", "VARCHAR(255)");
        ensureColumn(con, "contact_phone", "VARCHAR(50)");
        ensureColumn(con, "company_details", "TEXT");
        ensureColumn(con, "application_deadline", "DATE");
        ensureColumn(con, "logo_url", "VARCHAR(500)");
        ensureColumn(con, "address", "VARCHAR(255)");
        ensureColumn(con, "latitude", "DECIMAL(10, 7)");
        ensureColumn(con, "longitude", "DECIMAL(10, 7)");
        ensureColumn(con, "created_at", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");

        ensureIndex(con, "idx_jobs_employer_id", "employer_id");
        ensureIndex(con, "idx_jobs_employer_email", "employer_email");
        ensureIndex(con, "idx_jobs_coordinates", "latitude, longitude");
        ensureIndex(con, "idx_jobs_location", "location");
        ensureIndex(con, "idx_jobs_category", "category");
        ensureIndex(con, "idx_jobs_type", "type");
    }

    public static void ensureStudentProfilesCvColumn(Connection con) throws SQLException {
        ensureStudentProfilesTable(con);
    }

    public static void ensureApplicationsTable(Connection con) throws SQLException {
        ensureStudentProfilesCvColumn(con);

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS applications (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "job_id INT NOT NULL, " +
                            "student_id INT, " +
                            "student_email VARCHAR(255) NOT NULL, " +
                            "cv_link VARCHAR(500) NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "UNIQUE KEY unique_application (job_id, student_email), " +
                            "INDEX idx_applications_job_id (job_id), " +
                            "INDEX idx_applications_student_email (student_email)" +
                            ")"
            );
        }

        ensureColumn(con, "applications", "student_id", "INT");
        ensureIndex(con, "applications", "idx_applications_student_id", "student_id");
    }

    public static void ensureNotificationsTable(Connection con) throws SQLException {
        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS notifications (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "recipient_email VARCHAR(255) NOT NULL, " +
                            "recipient_account_type VARCHAR(20) NOT NULL, " +
                            "type VARCHAR(60) NOT NULL, " +
                            "title VARCHAR(160) NOT NULL, " +
                            "message VARCHAR(500) NOT NULL, " +
                            "target_url VARCHAR(500), " +
                            "is_read BOOLEAN NOT NULL DEFAULT FALSE, " +
                            "related_job_id INT, " +
                            "related_application_id INT, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "read_at TIMESTAMP NULL" +
                            ")"
            );
        }

        ensureIndex(con, "notifications", "idx_notifications_recipient",
                "recipient_email, recipient_account_type, is_read, created_at");
        ensureIndex(con, "notifications", "idx_notifications_related_job", "related_job_id");
        ensureIndex(con, "notifications", "idx_notifications_related_application",
                "related_application_id");
    }

    public static void ensureMessagingTables(Connection con) throws SQLException {
        ensureApplicationsTable(con);

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS conversations (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "application_id INT NOT NULL, " +
                            "job_id INT NOT NULL, " +
                            "employer_email VARCHAR(255) NOT NULL, " +
                            "student_email VARCHAR(255) NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "UNIQUE KEY unique_conversation_application (application_id), " +
                            "INDEX idx_conversations_employer (employer_email, last_message_at), " +
                            "INDEX idx_conversations_student (student_email, last_message_at), " +
                            "INDEX idx_conversations_job (job_id)" +
                            ")"
            );

            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS messages (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY, " +
                            "conversation_id INT NOT NULL, " +
                            "sender_email VARCHAR(255) NOT NULL, " +
                            "sender_account_type VARCHAR(20) NOT NULL, " +
                            "body TEXT NOT NULL, " +
                            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "read_at TIMESTAMP NULL, " +
                            "INDEX idx_messages_conversation (conversation_id, created_at), " +
                            "INDEX idx_messages_unread (conversation_id, sender_email, read_at)" +
                            ")"
            );
        }
    }

    private static void ensureColumn(Connection con,
                                     String tableName,
                                     String columnName,
                                     String definition) throws SQLException {
        if (columnExists(con, tableName, columnName)) {
            return;
        }

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + definition
            );
        } catch (SQLException e) {
            if (!isDuplicateColumn(e)) {
                throw e;
            }
        }
    }

    private static void ensureColumn(Connection con,
                                     String columnName,
                                     String definition) throws SQLException {
        ensureColumn(con, "jobs", columnName, definition);
    }

    private static boolean columnExists(Connection con,
                                        String tableName,
                                        String columnName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = ? " +
                "AND COLUMN_NAME = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static void ensureIndex(Connection con,
                                    String tableName,
                                    String indexName,
                                    String columns) throws SQLException {
        if (indexExists(con, tableName, indexName)) {
            return;
        }

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate(
                    "CREATE INDEX " + indexName + " ON " + tableName + " (" + columns + ")"
            );
        } catch (SQLException e) {
            if (!isDuplicateIndex(e)) {
                throw e;
            }
        }
    }

    private static void ensureIndex(Connection con,
                                    String indexName,
                                    String columns) throws SQLException {
        ensureIndex(con, "jobs", indexName, columns);
    }

    private static boolean indexExists(Connection con,
                                       String tableName,
                                       String indexName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS " +
                "WHERE TABLE_SCHEMA = DATABASE() " +
                "AND TABLE_NAME = ? " +
                "AND INDEX_NAME = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, indexName);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private static boolean isDuplicateColumn(SQLException e) {
        return "42S21".equals(e.getSQLState()) || hasMessage(e, "duplicate column");
    }

    private static boolean isDuplicateIndex(SQLException e) {
        return "42000".equals(e.getSQLState()) && hasMessage(e, "duplicate key name");
    }

    private static boolean hasMessage(SQLException e, String text) {
        return e.getMessage() != null
                && e.getMessage().toLowerCase().contains(text);
    }
}
