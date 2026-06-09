import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/TranslationServlet")
public class TranslationServlet extends HttpServlet {

    private final TranslationService translationService = new TranslationService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String language = TranslationService.normalizeLanguage(request.getParameter("target"));
        String[] keys = request.getParameterValues("key");
        String[] texts = request.getParameterValues("text");

        if (texts == null || texts.length == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"No text provided.\"}");
            return;
        }

        StringBuilder json = new StringBuilder();
        json.append("{\"language\":\"").append(escapeJson(language)).append("\",");

        if (texts.length == 1 && (keys == null || keys.length == 0)) {
            String translatedText = translationService.translateText(texts[0], language);
            json.append("\"translatedText\":\"").append(escapeJson(translatedText)).append("\"}");
            response.getWriter().write(json.toString());
            return;
        }

        json.append("\"translations\":{");

        for (int i = 0; i < texts.length; i++) {
            if (i > 0) {
                json.append(",");
            }

            String key = keys != null && i < keys.length && keys[i] != null && !keys[i].isBlank()
                    ? keys[i]
                    : "item" + i;
            String translatedText = translationService.translateText(texts[i], language);

            json.append("\"").append(escapeJson(key)).append("\":\"")
                    .append(escapeJson(translatedText)).append("\"");
        }

        json.append("}}");
        response.getWriter().write(json.toString());
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);

            switch (current) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (current < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) current));
                    } else {
                        escaped.append(current);
                    }
                    break;
            }
        }

        return escaped.toString();
    }
}
