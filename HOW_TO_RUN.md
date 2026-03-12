# Инструкция по запуску BoosterBot на чистом компьютере

Проект использует **Docker** для автоматического развертывания базы данных, поэтому ничего устанавливать вручную (кроме Java, Maven и Docker) не нужно. Скрипт создания таблиц (`db/init/init.sql`) выполняется **автоматически** при первом запуске контейнера с PostgreSQL.

## Шаг 1: Подготовка
Убедитесь, что на компьютере установлены:
- **Java 21**
- **Docker** и **Docker Compose**
- (Опционально) Git, чтобы склонировать репозиторий.

## Шаг 2: Настройка конфигурации
Откройте файл `src/main/resources/application.properties` (или создайте его, если он не попал в Git) и пропишите туда токен вашего бота:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/booster_db
spring.datasource.username=booster
spring.datasource.password=booster_password
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true

# Укажите имя и токен бота, полученные у BotFather
bot.name=YOUR_BOT_NAME_HERE
bot.token=YOUR_BOT_TOKEN_HERE
```

## Шаг 3: Запуск Базы Данных
Откройте терминал в корневой папке проекта (где находится `docker-compose.yml`) и выполните команду:

```bash
docker compose up -d
```
Эта команда скачает образ PostgreSQL, запустит его и **автоматически выполнит скрипт инициализации таблиц** из папки `db/init/init.sql`.

Вы можете проверить, что база запустилась, командой:
```bash
docker ps
```
Должен быть виден работающий контейнер `boosterbot-db`.

## Шаг 4: Запуск Бота
Когда база данных работает, вы можете запустить самого Telegram-бота.

Способ 1: Через среду разработки (IntelliJ IDEA)
Просто запустите главный класс `BoosterBotApplication.java`.

Способ 2: Через Maven из консоли
```bash
./mvnw spring-boot:run
```

Бот подключится к БД, подхватит созданные таблицы и начнет слушать сообщения в Telegram!

## Завершение работы
Чтобы остановить бота, нажмите `Ctrl + C` в терминале с запущенным ботом.  
Чтобы остановить базу данных, выполните:
```bash
docker compose down
```
