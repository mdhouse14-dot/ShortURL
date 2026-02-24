package app.storage;

import app.model.Notification;
import app.model.ShortLink;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

public class JsonStore {

    public static class Data {
        public Map<String, ShortLink> linksByCode = new HashMap<>();
        public List<Notification> notifications = new ArrayList<>();
    }

    private final Path file;
    private final ObjectMapper mapper;

    private Data data;

    public JsonStore(Path file) {
        this.file = file;
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .enable(SerializationFeature.INDENT_OUTPUT);
        this.data = new Data();
        load();
    }

    public synchronized void load() {
        if (!Files.exists(file)) {
            this.data = new Data();
            save();
            return;
        }
        try {
            this.data = mapper.readValue(file.toFile(), Data.class);
            if (this.data.linksByCode == null) this.data.linksByCode = new HashMap<>();
            if (this.data.notifications == null) this.data.notifications = new ArrayList<>();
        } catch (IOException e) {
            // Если файл битый — стартуем с пустого, но не падаем
            this.data = new Data();
        }
    }

    public synchronized void save() {
        try {
            mapper.writeValue(file.toFile(), data);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить хранилище: " + e.getMessage(), e);
        }
    }

    public synchronized Optional<ShortLink> findByCode(String code) {
        return Optional.ofNullable(data.linksByCode.get(code));
    }

    public synchronized void putLink(ShortLink link) {
        data.linksByCode.put(link.code, link);
        save();
    }

    public synchronized boolean containsCode(String code) {
        return data.linksByCode.containsKey(code);
    }

    public synchronized Collection<ShortLink> allLinks() {
        return new ArrayList<>(data.linksByCode.values());
    }

    public synchronized void removeLink(String code) {
        data.linksByCode.remove(code);
        save();
    }

    public synchronized List<ShortLink> linksByOwner(UUID userId) {
        List<ShortLink> out = new ArrayList<>();
        for (ShortLink l : data.linksByCode.values()) {
            if (userId.equals(l.ownerUserId)) out.add(l);
        }
        out.sort(Comparator.comparing(a -> a.createdAt));
        return out;
    }

    public synchronized void addNotification(Notification n) {
        data.notifications.add(n);
        save();
    }

    public synchronized List<Notification> notificationsByUser(UUID userId) {
        List<Notification> out = new ArrayList<>();
        for (Notification n : data.notifications) {
            if (userId.equals(n.userId)) out.add(n);
        }
        out.sort(Comparator.comparing(a -> a.createdAt));
        return out;
    }

    public synchronized void clearNotifications(UUID userId) {
        data.notifications.removeIf(n -> userId.equals(n.userId));
        save();
    }
}