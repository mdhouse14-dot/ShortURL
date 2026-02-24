# ShortURL

Cервис сокращения ссылок с поддержкой:
- уникальных коротких ссылок **для каждого пользователя**,
- лимита переходов,
- времени жизни ссылки (TTL),
- автоудаления старых и недействительных ссылок,
- уведомлений владельца при истечении TTL или исчерпании лимита.

# Как пользоваться сервисом

# 1) Запуск
**Через IntelliJ IDEA**
1. Откройте проект.
2. Откройте `src/main/java/app/Main.java`.
3. Запустите метод `Main`.

# 2) Поддерживаемые комманды.
1) Создать короткую ссылку
2) Перейти по короткой ссылке (открыть в браузере)
3) Мои ссылки
4) Удалить мою ссылку
5) Уведомления
0) Выход

# 3) Способы тестирования кода.

# Тест 1 — уникальные ссылки для разных пользователей:
- Создайте короткую ссылку на один и тот же URL.
- Закройте программу.
- Удалите файл .user_uuid в корне проекта, для имитации нового пользователя).
- Запустите снова и сократите тот же URL.
- Сравните полученные короткие ссылки.

# Тест 2 — лимит переходов:
- Создайте ссылку с лимитом, например 2.
- Выполните переход по ней 2 раза (меню 2).
- Попробуйте перейти 3-й раз.
  
# Тест 3 - TTL и автоудаление ссылок:
- Для быстрого тестирования замените код в файле src/main/java/app/config/AppConfig.java на "public static final Duration LINK_TTL = Duration.ofMinutes(1)".
- Запустите приложение.
- Создайте короткую ссылку.
- Подождите дольше 60 секунд.
- Попробуйте: перейти по ссылке, или посмотреть список ссылок.

  # ShortURL

Link shortening service with support:
- unique short links **for each user**,
- click-through limit,
- link lifetime (TTL),
- auto-deletion of old and invalid links,
- notifying the owner when the TTL expires or the limit is reached.

# How to use the service

#1) Launch
**Via IntelliJ IDEA**
1. Open the project.
2. Open the `src/main/java/app/Main.java `.
3. Run the `Main` method.

#2) Supported commands.
1) Create a short link
2) Follow the short link (open in the browser)
3) My links
4) Delete my link
5) Notifications
0) Exit

#3) Ways to test the code.

# Test 1 — Unique links for different users:
- Create a short link to the same URL.
- Close the program.
- Delete the file.user_uuid in the root of the project, to simulate a new user).
- Run it again and shorten the same URL.
- Compare the received short links.

# Test 2 — click-through limit:
- Create a link with a limit, for example 2.
- Click on it 2 times (menu 2).
- Try to cross the 3rd time.
  
# Test 3 - TTL and auto-delete links:
- For quick testing, replace the code in the src/main/java/app/config/AppConfig file.java to "public static final Duration LINK_TTL = Duration.ofMinutes(1)".
- Launch the app.
- Create a short link.
- Wait longer than 60 seconds.
- Try it: click on the link, or see the list of links.
