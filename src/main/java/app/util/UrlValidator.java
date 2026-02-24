package app.util;

import java.net.URI;

public final class UrlValidator {
    private UrlValidator() {}

    public static boolean isValid(String url) {
        try {
            URI uri = new URI(url);
            // Требуем http/https и host
            if (uri.getScheme() == null) return false;
            if (!uri.getScheme().equalsIgnoreCase("http") && !uri.getScheme().equalsIgnoreCase("https")) return false;
            return uri.getHost() != null && !uri.getHost().isBlank();
        } catch (Exception e) {
            return false;
        }
    }
}