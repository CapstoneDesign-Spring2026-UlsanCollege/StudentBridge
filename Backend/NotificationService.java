import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public final class NotificationService {

    private NotificationService() {
    }

    public static void createNotification(Connection con,
                                          String recipientEmail,
                                          String recipientAccountType,
                                          String type,
                                          String title,
                                          String message,
                                          String targetUrl,
                                          Integer relatedJobId,
                                          Integer relatedApplicationId)
            throws SQLException {

        String cleanRecipientEmail = clean(recipientEmail);

        if (cleanRecipientEmail.isEmpty()) {
            return;
        }

        DatabaseSchemaManager.ensureNotificationsTable(con);

        String sql = "INSERT INTO notifications " +
                "(recipient_email, recipient_account_type, type, title, message, target_url, " +
                "related_job_id, related_application_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cleanRecipientEmail);
            ps.setString(2, clean(recipientAccountType));
            ps.setString(3, clean(type));
            ps.setString(4, clean(title));
            ps.setString(5, clean(message));
            ps.setString(6, clean(targetUrl));
            setNullableInt(ps, 7, relatedJobId);
            setNullableInt(ps, 8, relatedApplicationId);
            ps.executeUpdate();
        }
    }

    private static void setNullableInt(PreparedStatement ps,
                                       int parameterIndex,
                                       Integer value) throws SQLException {

        if (value == null || value <= 0) {
            ps.setNull(parameterIndex, Types.INTEGER);
            return;
        }

        ps.setInt(parameterIndex, value);
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
