package app.service;

import app.config.AppConfig;
import app.model.ShortLink;
import app.storage.JsonStore;
import app.util.Base62;
import app.util.UrlValidator;

import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class ShortLinkService {

    private final JsonStore store;
    private final NotificationService notificationService;
    private final Random random = new Random();

    public ShortLinkService(JsonStore store, NotificationService notificationService) {
        this.store = store;
        this.notificationService = notificationService;
    }

    public String create(UUID userId, String longUrl, int maxClicks) {
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(longUrl, "longUrl");

        if (!UrlValidator.isValid(longUrl)) {
            throw new IllegalArgumentException("Некорректный URL. Нужен http/https и корректный host.");
        }
        if (maxClicks <= 0) {
            throw new IllegalArgumentException("Лимит переходов должен быть > 0");
        }

        String code = generateUniqueCode(userId, longUrl);
        ShortLink link = new ShortLink(code, longUrl, userId, maxClicks, Instant.now());
        store.putLink(link);

        return AppConfig.SHORT_DOMAIN + "/" + code;
    }

    public void openByShort(String shortOrCode) throws Exception {
        String code = extractCode(shortOrCode);

        ShortLink link = store.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена: " + shortOrCode));

        // TTL
        if (isExpired(link)) {
            store.removeLink(link.code);
            notificationService.notify(link.ownerUserId,
                    "Ссылка " + AppConfig.SHORT_DOMAIN + "/" + link.code + " удалена: истёк срок жизни (TTL).");
            throw new IllegalStateException("Ссылка протухла и удалена (TTL истёк).");
        }

        // Limit
        if (link.blocked || link.clicksUsed >= link.maxClicks) {
            link.blocked = true;
            store.putLink(link);
            notificationService.notify(link.ownerUserId,
                    "Ссылка " + AppConfig.SHORT_DOMAIN + "/" + link.code + " заблокирована: лимит переходов исчерпан.");
            throw new IllegalStateException("Ссылка недоступна: лимит переходов исчерпан.");
        }

        // Increment and open
        link.clicksUsed++;
        if (link.clicksUsed >= link.maxClicks) {
            link.blocked = true;
            notificationService.notify(link.ownerUserId,
                    "Ссылка " + AppConfig.SHORT_DOMAIN + "/" + link.code + " достигла лимита и теперь заблокирована.");
        }
        store.putLink(link);

        if (!Desktop.isDesktopSupported()) {
            throw new UnsupportedOperationException("Desktop не поддерживается в данной среде.");
        }
        Desktop.getDesktop().browse(new URI(link.longUrl));
    }

    public void delete(UUID userId, String shortOrCode) {
        String code = extractCode(shortOrCode);
        ShortLink link = store.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Ссылка не найдена."));

        if (!userId.equals(link.ownerUserId)) {
            throw new SecurityException("Удалять может только создатель ссылки (UUID владельца не совпадает).");
        }
        store.removeLink(code);
    }

    public boolean isExpired(ShortLink link) {
        Instant expiresAt = link.createdAt.plus(AppConfig.LINK_TTL);
        return Instant.now().isAfter(expiresAt);
    }

    private String extractCode(String shortOrCode) {
        String s = shortOrCode.trim();
        int slash = s.lastIndexOf('/');
        if (slash >= 0) {
            return s.substring(slash + 1);
        }
        return s;
    }

    private String generateUniqueCode(UUID userId, String longUrl) {
        // делаем несколько попыток, чтобы исключить коллизии
        for (int attempt = 0; attempt < 10_000; attempt++) {
            long salt = random.nextLong();
            long hash = fnv1a64(userId.toString() + "|" + longUrl + "|" + salt);
            // берём положительное число
            long positive = hash & Long.MAX_VALUE;
            String code = Base62.encode(positive);
            // укоротим код до 6-8 символов в среднем (можно настроить)
            if (code.length() > 8) code = code.substring(0, 8);

            if (!store.containsCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Не удалось сгенерировать уникальный код (слишком много коллизий).");
    }

    // Простой стабильный хеш (FNV-1a 64)
    private long fnv1a64(String input) {
        long hash = 0xcbf29ce484222325L;
        long prime = 0x100000001b3L;
        for (int i = 0; i < input.length(); i++) {
            hash ^= input.charAt(i);
            hash *= prime;
        }
        return hash;
    }
}