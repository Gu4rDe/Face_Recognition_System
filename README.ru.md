# Face Recognition System

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.3-green.svg)](https://github.com/JetBrains/compose-multiplatform)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg)]()

Десктопный клиент для системы распознавания лиц **Miit_FaceDetect**. Построен на **Compose Multiplatform** и **Material3**, предоставляет современный UI для аутентификации администратора, управления сотрудниками, распознавания лиц и настроек приложения.

## Содержание

- [Возможности](#возможности)
- [Технологический стек](#технологический-стек)
- [Архитектура](#архитектура)
- [Структура проекта](#структура-проекта)
- [Требования](#требования)
- [Установка](#установка)
- [Использование](#использование)
- [Конфигурация](#конфигурация)
- [Справочник по API](#справочник-по-api)
- [Участие в разработке](#участие-в-разработке)
- [Лицензия](#лицензия)

## Возможности

- **Аутентификация администратора** — вход, регистрация с инвайт-кодами, управление JWT Bearer-токеном
- **Управление сотрудниками** — CRUD-операции, поиск, захват фото через веб-камеру, статистика сотрудников
- **Распознавание лиц** — загрузка изображения или захват с веб-камеры, оверлей bounding box с цветовой индикацией уверенности, результаты сопоставления с данными сотрудников
- **Настройки** — переключение темы (светлая/тёмная), настраиваемый базовый URL API, параметры распознавания лиц (порог совпадения, разрешение камеры, FPS), постоянное хранение
- **Навигация** — навигация на основе Voyager с переключением секций

## Технологический стек

| Слой | Технология | Версия |
|------|-----------|--------|
| Язык | Kotlin | 2.3.20 |
| UI-фреймворк | Compose Multiplatform | 1.10.3 |
| UI-тулкит | Material3 | 1.10.0-alpha05 |
| Навигация | Voyager | 1.1.0-beta01 |
| HTTP-клиент | Ktor Client (CIO) | 3.0.3 |
| Сериализация | kotlinx-serialization-json | 1.7.3 |
| Корутины | kotlinx-coroutines-swing | 1.10.2 |
| Lifecycle | androidx.lifecycle | 2.10.0 |
| Логирование | SLF4J | 2.0.16 |
| Тестирование | JUnit | 4.13.2 |
| Веб-камера | webcam-capture | — |
| Сборка | Gradle (Kotlin Multiplatform) | — |

## Архитектура

Чистая архитектура (Clean Architecture) с тремя слоями и строгими правилами зависимостей:

```
presentation ──→ domain ←── data
```

| Слой | Ответственность |
|------|----------------|
| **presentation** | UI-компоненты, экраны Voyager, SettingsState, темы |
| **domain** | Бизнес-модели, интерфейсы репозиториев (без внешних зависимостей) |
| **data** | DTO, мапперы, ApiClient, ApiService, LocalSettingsStorage, реализации репозиториев |

**Правило зависимостей:** `presentation → domain ← data`. Слой domain не знает о data и presentation.

**Вспомогательные компоненты:**
- **ServiceLocator** — singleton DI-контейнер для ApiClient, ApiService и всех репозиториев
- **util/ErrorMapper** — преобразует ApiException/NetworkException в пользовательские сообщения

## Структура проекта

```
composeApp/src/jvmMain/kotlin/com/example/kotlinapp
├── main.kt                    # Точка входа (Window)
├── App.kt                     # Корневой composable
├── ServiceLocator.kt          # Singleton DI-контейнер
├── domain/
│   ├── model/                  # Бизнесовые классы данных
│   └── repository/             # Интерфейсы репозиториев
├── data/
│   ├── local/                  # Локальное хранение настроек
│   ├── dto/                    # Сериализуемые DTO для API
│   ├── mapper/                 # Мапперы DTO ↔ Domain
│   ├── remote/                 # Сетевой слой (ApiClient, ApiService)
│   └── repository/             # Реализации репозиториев
├── screen/                     # Voyager экраны
├── presentation/               # Управление состоянием UI
├── ui/                         # Переиспользуемые UI-компоненты
│   ├── buttons/
│   ├── textfields/
│   ├── icons/
│   ├── settings/
│   └── theme/
└── util/                       # Утилиты
```

## Требования

| Требование | Версия | Примечание |
|------------|--------|------------|
| JDK | 17+ | Требуется для JVM-таргета |
| Gradle | Встроен | Обёртка включена (`gradlew` / `gradlew.bat`) |
| ОС | Windows / macOS / Linux | Только десктопные таргеты |

## Установка

1. **Клонируйте репозиторий**
   ```bash
   git clone <repository-url>
   cd my-frontend-project
   ```

2. **Проверьте установку JDK**
   ```bash
   java -version
   ```
   Убедитесь, что JDK 17 или выше установлен и `JAVA_HOME` настроен.

3. **Соберите проект**
   ```bash
   ./gradlew build
   ```
   На Windows используйте `gradlew.bat build`.

## Использование

### Запуск в режиме разработки

```bash
./gradlew run
```

Запускает десктопное приложение с включённым hot reload.

### Сборка дистрибутива

```bash
./gradlew packageDistributable
```

Создаёт дистрибутив для текущей ОС (`.dmg` для macOS, `.msi` для Windows, `.deb` для Linux).

### Сборка установщика (Windows)

```bash
./gradlew packageMsi
```

Генерирует MSI-установщик для Windows.

### Запуск тестов

```bash
./gradlew allTests
```

### Точка входа

- **Главный класс:** `com.example.kotlinapp.MainKt`
- **Поток:** `main()` → `Window` → `App()` composable

## Конфигурация

### Настройки во время выполнения

Приложение предоставляет оверлей настроек для:

| Настройка | Описание | По умолчанию |
|-----------|----------|--------------|
| Тема | Светлая / Тёмная | Системная |
| Базовый URL API | URL бэкенд-сервера | `http://localhost:8000` |
| Порог совпадения | Минимальное сходство для распознавания лица | — |
| Разрешение камеры | Разрешение захвата веб-камеры | — |
| FPS | Кадров в секунду | — |

Настройки сохраняются через `java.util.prefs.Preferences`.

### Сеть

- **Движок:** Ktor CIO
- **Аутентификация:** JWT Bearer-токен в заголовке `Authorization`
- **Сериализация:** `kotlinx-serialization` с `ignoreUnknownKeys = true`, `isLenient = true`
- **Загрузки:** Multipart для фото сотрудников и изображений распознавания лиц

## Справочник по API

**Базовый URL:** `http://localhost:8000`

### Аутентификация

| Метод | Эндпоинт | Auth | Тело | Ответ |
|-------|----------|------|------|-------|
| POST | `/api/v1/admins/login` | — | `{username, password}` | `{access_token, token_type}` |
| POST | `/api/v1/admins/register` | — | `{username, email, password, invite_code}` | `{id, username, email, created_at}` |
| GET | `/api/v1/admins/me` | Да | — | `{id, username, email, created_at}` |

### Сотрудники

| Метод | Эндпоинт | Auth | Тело | Ответ |
|-------|----------|------|------|-------|
| POST | `/api/v1/employees/register` | Да | Multipart (поля + изображение) | `{EmployeeResponse}` |
| GET | `/api/v1/employees` | Да | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/search?q=` | Да | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/stats` | Да | — | `{total, active, inactive}` |
| PUT | `/api/v1/employees/{id}` | Да | `{EmployeeUpdate}` | `{EmployeeResponse}` |
| DELETE | `/api/v1/employees/{id}` | Да | — | 204 |

### Распознавание лиц

| Метод | Эндпоинт | Auth | Тело | Ответ |
|-------|----------|------|------|-------|
| POST | `/api/v1/faces/recognize` | Да | Multipart (изображение) | `{faces_detected, results}` |

### Инвайт-коды

| Метод | Эндпоинт | Auth | Тело | Ответ |
|-------|----------|------|------|-------|
| POST | `/api/v1/admin/invites` | Да | `{expires_hours}` | `{InviteCodeResponse}` |
| GET | `/api/v1/admin/invites` | Да | — | `[{InviteCodeResponse}]` |
| DELETE | `/api/v1/admin/invites/{id}` | Да | — | 204 |

### Проверка здоровья сервера

| Метод | Эндпоинт | Auth | Ответ |
|-------|----------|------|-------|
| GET | `/health` | — | `{status: "ok"}` |

## Участие в разработке

1. Форкните репозиторий
2. Создайте ветку с функциональностью (`git checkout -b feature/amazing-feature`)
3. Зафиксируйте изменения (`git commit -m 'Add amazing feature'`)
4. Отправьте в ветку (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## Лицензия

Этот проект распространяется под лицензией MIT. Подробнее см. [LICENSE](LICENSE).
