package app.service;

import app.config.AppConfig;
import app.model.ShortLink;
import app.storage.JsonStore;

import java.time.Instant;
import java.util.concurrent.*;

public class PurgeScheduler {

    private final JsonStore store;
    private final NotificationService notificationService;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PurgeScheduler(JsonStore store, NotificationService notificationService) {
        this.store = store;
        this.notificationService = notificationService;
    }

    public void start() {
        executor.scheduleAtFixedRate(this::purgeExpired, 0,
                AppConfig.PURGE_INTERVAL.toSeconds(), TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }

    private void purgeExpired() {
        try {
            Instant now = Instant.now();
            for (ShortLink link : store.allLinks()) {
                Instant expiresAt = link.createdAt.plus(AppConfig.LINK_TTL);
                if (now.isAfter(expiresAt)) {
                    store.removeLink(link.code);
                    notificationService.notify(link.ownerUserId,
                            "Ссылка " + AppConfig.SHORT_DOMAIN + "/" + link.code + " удалена автоматически: TTL истёк.");
                }
            }
        } catch (Exception ignored) {
            // Периодическая задача не должна валить приложение
        }
    }
}