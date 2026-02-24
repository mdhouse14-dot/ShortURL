package app.service;

import app.config.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class UserService {

    public UUID getOrCreateUserId() {
        if (Files.exists(AppConfig.USER_FILE)) {
            try {
                String s = Files.readString(AppConfig.USER_FILE).trim();
                return UUID.fromString(s);
            } catch (Exception ignored) {
                // если файл поврежден — создадим новый
            }
        }
        UUID id = UUID.randomUUID();
        try {
            Files.writeString(AppConfig.USER_FILE, id.toString());
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить UUID пользователя: " + e.getMessage(), e);
        }
        return id;
    }
}