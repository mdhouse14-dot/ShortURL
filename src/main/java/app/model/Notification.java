package app.model;

import java.time.Instant;
import java.util.UUID;

public class Notification {
    public UUID userId;
    public Instant createdAt;
    public String message;

    public Notification() {}

    public Notification(UUID userId, Instant createdAt, String message) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.message = message;
    }
}