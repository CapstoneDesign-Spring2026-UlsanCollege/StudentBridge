import java.sql.Connection;
import java.sql.DriverManager;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DBConnection {

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            DatabaseConfig config = getDatabaseConfig();
            Connection con = DriverManager.getConnection(config.url, config.user, config.password);

            System.out.println("Connected to MySQL.");
            return con;

        } catch (Exception e) {
            System.out.println("Connection to MySQL failed.");
            e.printStackTrace();
            return null;
        }
    }

    private static DatabaseConfig getDatabaseConfig() {
        String user = firstNonBlank(env("DB_USER"), env("MYSQL_USER"), "root");
        String password = firstNonBlank(env("DB_PASSWORD"), env("MYSQL_PASSWORD"), "");
        String explicitUrl = firstNonBlank(env("DB_URL"), env("JDBC_DATABASE_URL"), env("DATABASE_URL"));

        if (!explicitUrl.isEmpty()) {
            return normalizeDatabaseUrl(explicitUrl, user, password);
        }

        String host = firstNonBlank(env("DB_HOST"), env("MYSQL_HOST"), "localhost");
        String port = firstNonBlank(env("DB_PORT"), env("MYSQL_PORT"), "3306");
        String databaseName = firstNonBlank(env("DB_NAME"), env("MYSQL_DATABASE"), "studentbridge");
        String sslMode = firstNonBlank(env("DB_SSL_MODE"), env("MYSQL_SSL_MODE"));

        StringBuilder url = new StringBuilder("jdbc:mysql://")
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(databaseName)
                .append("?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8");

        if (!sslMode.isEmpty()) {
            url.append("&sslMode=").append(sslMode);
        }

        return new DatabaseConfig(url.toString(), user, password);
    }

    private static DatabaseConfig normalizeDatabaseUrl(String rawUrl, String fallbackUser, String fallbackPassword) {
        if (rawUrl.startsWith("jdbc:mysql://")) {
            return new DatabaseConfig(rawUrl, fallbackUser, fallbackPassword);
        }

        if (!rawUrl.startsWith("mysql://")) {
            return new DatabaseConfig(rawUrl, fallbackUser, fallbackPassword);
        }

        try {
            URI uri = URI.create(rawUrl);
            String path = uri.getPath() == null ? "" : uri.getPath();
            String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
            String port = uri.getPort() == -1 ? "" : ":" + uri.getPort();
            String jdbcUrl = "jdbc:mysql://" + uri.getHost() + port + path + query;

            String user = fallbackUser;
            String password = fallbackPassword;

            if (uri.getUserInfo() != null) {
                String[] credentials = uri.getUserInfo().split(":", 2);
                user = decode(credentials[0]);

                if (credentials.length > 1) {
                    password = decode(credentials[1]);
                }
            }

            return new DatabaseConfig(jdbcUrl, user, password);
        } catch (IllegalArgumentException e) {
            return new DatabaseConfig(rawUrl, fallbackUser, fallbackPassword);
        }
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private static String env(String key) {
        String value = System.getenv(key);
        return value == null ? "" : value.trim();
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }

        return "";
    }

    private static class DatabaseConfig {
        private final String url;
        private final String user;
        private final String password;

        private DatabaseConfig(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;
        }
    }
}
