package app.model;

import java.time.Instant;
import java.util.UUID;

public class ShortLink {
    public String code;
    public String longUrl;

    public UUID ownerUserId;

    public int maxClicks;      // лимит
    public int clicksUsed;     // сколько уже использовали

    public Instant createdAt;  // для TTL
    public boolean blocked;    // если лимит исчерпан (или вручную)

    public ShortLink() {}

    public ShortLink(String code, String longUrl, UUID ownerUserId, int maxClicks, Instant createdAt) {
        this.code = code;
        this.longUrl = longUrl;
        this.ownerUserId = ownerUserId;
        this.maxClicks = maxClicks;
        this.clicksUsed = 0;
        this.createdAt = createdAt;
        this.blocked = false;
    }
}