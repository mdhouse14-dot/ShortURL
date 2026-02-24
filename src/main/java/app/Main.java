package app;

import app.config.AppConfig;
import app.model.Notification;
import app.model.ShortLink;
import app.service.NotificationService;
import app.service.PurgeScheduler;
import app.service.ShortLinkService;
import app.service.UserService;
import app.storage.JsonStore;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    public static void main(String[] args) {
        JsonStore store = new JsonStore(AppConfig.DATA_FILE);

        UserService userService = new UserService();
        UUID userId = userService.getOrCreateUserId();

        NotificationService notificationService = new NotificationService(store);
        ShortLinkService shortLinkService = new ShortLinkService(store, notificationService);

        PurgeScheduler purgeScheduler = new PurgeScheduler(store, notificationService);
        purgeScheduler.start();

        System.out.println("=== URL Shortener (Console) ===");
        System.out.println("Ваш UUID: " + userId);
        System.out.println("TTL ссылок: " + AppConfig.LINK_TTL);
        System.out.println("Домен коротких ссылок: " + AppConfig.SHORT_DOMAIN);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.println("\nМеню:");
                System.out.println("1) Создать короткую ссылку");
                System.out.println("2) Перейти по короткой ссылке (открыть в браузере)");
                System.out.println("3) Мои ссылки");
                System.out.println("4) Удалить мою ссылку");
                System.out.println("5) Уведомления");
                System.out.println("0) Выход");
                System.out.print("Выберите: ");

                String choice = sc.nextLine().trim();

                try {
                    switch (choice) {
                        case "1" -> {
                            System.out.print("Введите длинный URL: ");
                            String longUrl = sc.nextLine().trim();
                            System.out.print("Лимит переходов (целое > 0): ");
                            int max = Integer.parseInt(sc.nextLine().trim());

                            String shortUrl = shortLinkService.create(userId, longUrl, max);
                            System.out.println("Готово! Короткая ссылка: " + shortUrl);
                        }
                        case "2" -> {
                            System.out.print("Введите короткую ссылку или код: ");
                            String s = sc.nextLine().trim();
                            shortLinkService.openByShort(s);
                            System.out.println("Открыто в браузере (если система это поддерживает).");
                        }
                        case "3" -> {
                            List<ShortLink> links = store.linksByOwner(userId);
                            if (links.isEmpty()) {
                                System.out.println("У вас пока нет ссылок.");
                            } else {
                                System.out.println("Ваши ссылки:");
                                for (ShortLink l : links) {
                                    System.out.println("- " + AppConfig.SHORT_DOMAIN + "/" + l.code +
                                            " | clicks: " + l.clicksUsed + "/" + l.maxClicks +
                                            " | blocked=" + l.blocked +
                                            " | url=" + l.longUrl +
                                            " | createdAt=" + l.createdAt);
                                }
                            }
                        }
                        case "4" -> {
                            System.out.print("Введите короткую ссылку или код для удаления: ");
                            String s = sc.nextLine().trim();
                            shortLinkService.delete(userId, s);
                            System.out.println("Удалено.");
                        }
                        case "5" -> {
                            List<Notification> ns = notificationService.list(userId);
                            if (ns.isEmpty()) {
                                System.out.println("Уведомлений нет.");
                            } else {
                                System.out.println("Уведомления:");
                                for (Notification n : ns) {
                                    System.out.println("- [" + n.createdAt + "] " + n.message);
                                }
                                System.out.print("Очистить уведомления? (y/n): ");
                                String ans = sc.nextLine().trim();
                                if (ans.equalsIgnoreCase("y")) {
                                    notificationService.clear(userId);
                                    System.out.println("Очищено.");
                                }
                            }
                        }
                        case "0" -> {
                            System.out.println("Выход.");
                            purgeScheduler.stop();
                            return;
                        }
                        default -> System.out.println("Неизвестный пункт меню.");
                    }
                } catch (Exception ex) {
                    System.out.println("Ошибка: " + ex.getMessage());
                }
            }
        }
    }
}