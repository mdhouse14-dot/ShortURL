package app.config;

import java.nio.file.Path;
import java.time.Duration;

public final class AppConfig {
    private AppConfig() {}

    // Имитация домена
    public static final String SHORT_DOMAIN = "clck.ru";

    // TTL задаётся системой. Для теста удобно поставить 1 час.
    // Для "сутки" поменяйте на Duration.ofHours(24)
    public static final Duration LINK_TTL = Duration.ofHours(1);

    // Где хранить данные (в папке проекта)
    public static final Path DATA_FILE = Path.of("data.json");

    // Где хранить UUID пользователя на данном компьютере
    public static final Path USER_FILE = Path.of(".user_uuid");

    // Как часто чистить протухшие ссылки
    public static final Duration PURGE_INTERVAL = Duration.ofSeconds(10);
}