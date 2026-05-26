import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class TranslationService {

    private static final String API_KEY_ENV = "GOOGLE_TRANSLATE_API_KEY";
    private static final String SOURCE_LANGUAGE = "auto";
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "ko", "ne", "bn");

    public static String normalizeLanguage(String requestedLanguage) {
        if (requestedLanguage == null || requestedLanguage.isBlank()) {
            return "en";
        }

        String language = requestedLanguage.trim().toLowerCase();
        return SUPPORTED_LANGUAGES.contains(language) ? language : "en";
    }

    public String translate(Connection con, String text, String targetLanguage) {
        String normalizedLanguage = normalizeLanguage(targetLanguage);

        if (text == null || text.isBlank() || "en".equals(normalizedLanguage)) {
            return text == null ? "" : text;
        }

        String hash = sha256(text);
        String cachedTranslation = findCachedTranslation(con, hash, normalizedLanguage);

        if (cachedTranslation != null && !cachedTranslation.isBlank()) {
            return cachedTranslation;
        }

        String apiKey = System.getenv(API_KEY_ENV);

        if (apiKey == null || apiKey.isBlank()) {
            return text;
        }

        try {
            String translatedText = requestGoogleTranslation(apiKey, text, normalizedLanguage);

            if (translatedText == null || translatedText.isBlank()) {
                return text;
            }

            saveCachedTranslation(con, hash, text, normalizedLanguage, translatedText);
            return translatedText;
        } catch (Exception e) {
            System.out.println("Translation failed. Returning original text.");
            e.printStackTrace();
            return text;
        }
    }

    private String findCachedTranslation(Connection con, String hash, String targetLanguage) {
        String sql = "SELECT translated_text FROM translation_cache "
                + "WHERE source_text_hash = ? AND source_lang = ? AND target_lang = ? LIMIT 1";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, SOURCE_LANGUAGE);
            ps.setString(3, targetLanguage);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("translated_text");
                }
            }
        } catch (SQLException e) {
            // The cache table is optional until docs/database_update_i18n.sql is applied.
            return null;
        }

        return null;
    }

    private void saveCachedTranslation(Connection con, String hash, String sourceText, String targetLanguage,
            String translatedText) {

        String sql = "INSERT INTO translation_cache "
                + "(source_text_hash, source_text, source_lang, target_lang, translated_text) "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE translated_text = VALUES(translated_text)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setString(2, sourceText);
            ps.setString(3, SOURCE_LANGUAGE);
            ps.setString(4, targetLanguage);
            ps.setString(5, translatedText);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Translation still works without cache, so do not fail the request.
            System.out.println("Translation cache save skipped.");
        }
    }

    private String requestGoogleTranslation(String apiKey, String text, String targetLanguage) throws IOException {
        URI uri = URI.create("https://translation.googleapis.com/language/translate/v2?key="
                + URLEncoder.encode(apiKey, StandardCharsets.UTF_8));

        String body = "q=" + URLEncoder.encode(text, StandardCharsets.UTF_8)
                + "&target=" + URLEncoder.encode(targetLanguage, StandardCharsets.UTF_8)
                + "&format=text";

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(8000);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = connection.getResponseCode();
        String responseBody = readResponse(connection, statusCode);

        if (statusCode < 200 || statusCode >= 300) {
            throw new IOException("Google Translation API returned " + statusCode + ": " + responseBody);
        }

        return decodeBasicHtmlEntities(extractTranslatedText(responseBody));
    }

    private String readResponse(HttpURLConnection connection, int statusCode) throws IOException {
        InputStreamReader inputStreamReader = statusCode >= 200 && statusCode < 300
                ? new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                : new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);

        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return response.toString();
        }
    }

    private String extractTranslatedText(String responseBody) {
        String key = "\"translatedText\"";
        int keyIndex = responseBody.indexOf(key);

        if (keyIndex < 0) {
            return "";
        }

        int colonIndex = responseBody.indexOf(":", keyIndex);
        int startQuoteIndex = responseBody.indexOf("\"", colonIndex + 1);

        if (colonIndex < 0 || startQuoteIndex < 0) {
            return "";
        }

        StringBuilder value = new StringBuilder();
        boolean escaped = false;

        for (int i = startQuoteIndex + 1; i < responseBody.length(); i++) {
            char current = responseBody.charAt(i);

            if (escaped) {
                appendEscapedCharacter(value, current, responseBody, i);

                if (current == 'u') {
                    i += 4;
                }

                escaped = false;
                continue;
            }

            if (current == '\\') {
                escaped = true;
                continue;
            }

            if (current == '"') {
                break;
            }

            value.append(current);
        }

        return value.toString();
    }

    private void appendEscapedCharacter(StringBuilder value, char current, String source, int index) {
        switch (current) {
            case '"':
                value.append('"');
                break;
            case '\\':
                value.append('\\');
                break;
            case '/':
                value.append('/');
                break;
            case 'b':
                value.append('\b');
                break;
            case 'f':
                value.append('\f');
                break;
            case 'n':
                value.append('\n');
                break;
            case 'r':
                value.append('\r');
                break;
            case 't':
                value.append('\t');
                break;
            case 'u':
                if (index + 4 < source.length()) {
                    String hex = source.substring(index + 1, index + 5);
                    value.append((char) Integer.parseInt(hex, 16));
                }
                break;
            default:
                value.append(current);
                break;
        }
    }

    private String decodeBasicHtmlEntities(String value) {
        return value
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();

            for (byte current : encodedHash) {
                hex.append(String.format("%02x", current));
            }

            return hex.toString();
        } catch (Exception e) {
            return String.valueOf(value.hashCode());
        }
    }
}
