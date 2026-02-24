package app.service;

import app.model.Notification;
import app.storage.JsonStore;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class NotificationService {
    private final JsonStore store;

    public NotificationService(JsonStore store) {
        this.store = store;
    }

    public void notify(UUID userId, String message) {
        store.addNotification(new Notification(userId, Instant.now(), message));
    }

    public List<Notification> list(UUID userId) {
        return store.notificationsByUser(userId);
    }

    public void clear(UUID userId) {
        store.clearNotifications(userId);
    }
}