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
| Читаемость | Пайплайн — явная последовательность шагов |
| Разделение анализа и генерации | Фильтры только проставляют теги; генератор использует их |

> ⚠️ Важное архитектурное разграничение: в этом проекте фильтры **только анализируют** входные данные и проставляют теги в `ProcessContext`. Генерацию ответа выполняет отдельный компонент (`ComplimentService`), который является **генератором**, а не фильтром. `DatabaseStorageFilter` является **стоком (Sink)** — он потребляет финальный контекст и ничего не возвращает в пайплайн.

### Пайплайн запроса

```
Telegram Update
      │
      ▼
BoosterBot (контроллер, Dispatcher)
      │
      ▼  pipeline.getBoost(chatId, message, name)
      │
      ╔══════════════════════════════╗
      ║   FILTERS — Фаза анализа    ║
      ╠══════════════════════════════╣
      ├── [@Order 0] WeekdayAnalyzer       → tag: "weekend"
      ├── [@Order 1] NameExtractor          → находит/создаёт User в БД
      ├── [@Order 1] TimeOfDayAnalyzer      → tag: "morning"/"day"/"evening"/"night"
      ├── [@Order 2] MoodDetector           → tag: "success"/"super_success"/"sad"/"super_sad"/"neutral"
      │                                      (TF-оценка + усилители + нормализация по sqrt(n))
      └── [@Order 3] UnknownMessageHandler  → tag: "unknown" если нет эмоциональных тегов
      │
      ╔══════════════════════════════╗
      ║  PROCESSOR — Фаза генерации ║
      ╠══════════════════════════════╣
      ├── [@Order 4] ComplimentService      → GENERATOR: выбирает фразу из JSON по тегам
      │                                      взвешенный рандом по user_preferences
      └── [@Order 5] PersonalizationProcessor → TRANSFORMER: подставляет имя в шаблон
      │
      ╔══════════════════════════════╗
      ║      SINK — Сохранение       ║
      ╠══════════════════════════════╣
      └── [@Order 7] DatabaseStorage        → сохраняет историю и статистику в PostgreSQL
      │
      ▼
BoostResult(text, phraseHash, tags)
      │
      ▼
BoosterBot: форматирование (emoji по тегу) + отправка + inline кнопки 👍/👎
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