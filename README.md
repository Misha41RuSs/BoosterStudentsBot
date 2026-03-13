# BoosterStudentsBot

Telegram-бот для поддержки эмоционального состояния студентов. Бот анализирует сообщения в чате, определяет настроение пользователя и отвечает персонализированными мотивационными фразами с учётом времени суток, дня недели и истории предпочтений.

## Возможности

- 🧠 **Анализ настроения** — 5 градаций: `super_success`, `success`, `neutral`, `sad`, `super_sad`
- 🕐 **Контекст времени** — утро / день / вечер / ночь / выходные
- 👤 **Персонализация** — обращение по имени пользователя из Telegram
- 👍👎 **Обратная связь** — пользователь может лайкнуть или дизлайкнуть ответ
- 🎲 **Взвешенный рандом** — понравившиеся фразы выпадают в 3x чаще, нелюбимые — почти никогда
- 💾 **Хранение данных** — PostgreSQL через Spring Data JPA; история, статистика и предпочтения сохраняются между перезапусками
- 📋 **Логирование** — SLF4J + Logback, все запросы и их обработка логируются в консоль

## Архитектура: Pipes & Filters

Проект построен на паттерне **«Каналы и фильтры» (Pipes & Filters)**. Это означает, что каждый запрос проходит через цепочку независимых обработчиков — фильтров. Каждый фильтр получает общий контекст (`ProcessContext`), обогащает его данными (тегами, именем пользователя, найденной фразой) и передаёт дальше.

### Почему Pipes & Filters?

| Требование | Как покрывается паттерном |
|---|---|
| Независимость контекстов (время, настроение) | Каждый фильтр отвечает строго за свою область |
| Расширяемость | Новый фильтр = новый `@Component` + `@Order` |
| Тестируемость | Каждый фильтр тестируется изолированно |
| Читаемость | Пайплайн — это явная последовательность шагов |

### Пайплайн запроса

```
Telegram Update
      │
      ▼
BoosterBot (контроллер)
      │
      ▼  pipeline.getBoost(chatId, message, name)
      │
      ├── [@Order 0] WeekdayFilter      → tag: "weekend" (если сб/вс)
      │
      ├── [@Order 1] NameExtractorFilter → находит/создаёт пользователя в БД
      │
      ├── [@Order 1] TimeOfDayFilter     → tag: "morning" / "day" / "evening" / "night"
      │
      ├── [@Order 2] MoodDetectorFilter  → tag: "success"/"super_success"/"sad"/"super_sad"/"neutral"
      │                                   (TF-анализ слов + усилители + нормализация по sqrt(n))
      │
      ├── [@Order 3] UnknownMessageFilter → tag: "unknown" если нет эмоций
      │
      ├── [@Order 4] ComplimentService   → выбор фразы из JSON по тегам + взвешенный рандом по предпочтениям
      │
      ├── [@Order 5] PersonalizationFilter → подстановка имени пользователя в шаблон %s
      │
      └── [@Order 7] DatabaseStorageFilter → сохранение истории и статистики в PostgreSQL
            │
            ▼
      BoostResult(text, phraseHash, tags)
            │
            ▼
      Emoji форматирование по тегу (в контроллере)
            │
            ▼
      Ответ + inline кнопки 👍 / 👎
```

### Ключевые классы

| Класс | Роль |
|---|---|
| `ProcessContext` | Общий контекст данных, передаётся через все фильтры |
| `Pipeline` | Запускает фильтры по порядку `@Order`, возвращает `BoostResult` |
| `BoostResult` | DTO: текст ответа + хеш фразы + теги для форматирования |
| `ComplimentService` | Загружает `compliments.json`, взвешенный выбор фразы |
| `UserPreference` | JPA entity: хранит score (+1/-1) для каждой фразы по пользователю |
| `MoodDetectorFilter` | TF-анализ + усилители + нормализация → 5 градаций настроения |

### Структура базы данных

```
users               user_stats          history
─────────────       ──────────────      ────────────────
id                  id                  id
chat_id             user_id (FK)        user_id (FK)
first_name          last_mood           message_text
username            boosts_received     detected_mood
registered_at       updated_at          response_type
                                        created_at

user_preferences
─────────────────
id
user_id (FK)
phrase_hash         ← String.valueOf(phrase.hashCode())
score               ← +1 (лайк) / -1 (дизлайк)
created_at
```

## Запуск

### Требования

- Java 21+
- Docker (для PostgreSQL)
- Telegram Bot Token

### Шаги

```bash
# 1. Запустить базу данных
docker compose up -d

# 2. Запустить бот
./mvnw spring-boot:run
```

Настройки в `src/main/resources/application.properties`.

## Расширение коллекции фраз

Редактируйте `src/main/resources/compliments.json` — без изменения кода.  
Ключи соответствуют тегам пайплайна: `super_success`, `success`, `neutral`, `sad`, `super_sad`, `morning`, `evening`, `weekend`, `unknown`, `default`.

## Тесты

```bash
./mvnw test
```

Unit-тесты охватывают все 5 градаций `MoodDetectorFilter` (класс `MoodDetectorFilterTest`).