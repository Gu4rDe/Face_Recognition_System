# KotlinApp — Обзор проекта

## 1. Обзор

KotlinApp — десктопный клиент для системы распознавания лиц **Miit_FaceDetect**. Построен на **Compose Multiplatform** (JVM-таргет) с **Material3**, предоставляет UI для аутентификации администратора, управления сотрудниками, распознавания лиц и настроек приложения. Навигация — **Voyager**, внедрение зависимостей — **ServiceLocator**, локальное хранение настроек — **LocalSettingsStorage** (java.util.prefs.Preferences). Всё взаимодействие с сервером — через FastAPI.

---

## 2. Технологический стек

| Слой | Технология | Версия |
|------|-----------|--------|
| Язык | Kotlin | 2.3.20 |
| UI-фреймворк | Compose Multiplatform | 1.10.3 |
| UI-тулкит | Material3 | 1.10.0-alpha05 |
| Навигация | Voyager | 1.1.0-beta01 |
| HTTP-клиент | Ktor Client (CIO engine) | 3.0.3 |
| Сериализация | kotlinx-serialization-json | 1.7.3 |
| Корутины | kotlinx-coroutines-swing | 1.10.2 |
| Lifecycle | androidx.lifecycle (viewmodel, runtime) | 2.10.0 |
| Логирование (backend) | SLF4J | 2.0.16 |
| Тестирование | JUnit | 4.13.2 |
| Hot Reload | Compose Hot Reload | 1.0.0 |
| Сборка | Gradle (Kotlin Multiplatform) | — |
| Десктоп-упаковка | Compose Desktop (Dmg, Msi, Deb) | — |

---

## 3. Архитектура

Чистая архитектура (Clean Architecture) с тремя слоями и строгими правилами зависимостей:

```
┌─────────────────────────────────────────────────┐
│                 presentation                      │
│   UI-компонуемые, Screen'ы (Voyager),            │
│   SettingsState, тема                            │
│   зависит от → domain                            │
├─────────────────────────────────────────────────┤
│                   domain                          │
│   Бизнес-модели, интерфейсы репозиториев         │
│   нет внешних зависимостей                       │
├─────────────────────────────────────────────────┤
│                    data                           │
│   DTO, мапперы, ApiClient, ApiService,           │
│   LocalSettingsStorage, реализации репозиториев  │
│   зависит от → domain                            │
└─────────────────────────────────────────────────┘
```

**Правило зависимостей:** `presentation → domain ← data`. Слой domain не знает о data и presentation.

**Вспомогательные компоненты вне слоёв:**
- **ServiceLocator** — singleton DI-контейнер, создаёт и предоставляет ApiClient, ApiService, все репозитории
- **api/ApiService** — тестовый/демо-клиент для `dummyjson.com` (отдельный от data.remote.ApiService)
- **util/ErrorMapper** — преобразует ApiException/NetworkException в пользовательские сообщения на русском

### Структура пакетов

```
com.example.kotlinapp
├── main.kt                    # Точка входа (Window)
├── App.kt                     # Корневой composable
├── ServiceLocator.kt          # Singleton DI-контейнер
├── domain/
│   ├── model/                  # Бизнесовые классы данных
│   │   ├── Admin.kt            # Admin, AdminRegister, AdminLogin
│   │   ├── AuthResult.kt       # AuthResult (token response)
│   │   ├── Employee.kt         # Employee, EmployeeCreate, EmployeeUpdate, EmployeeStats
│   │   ├── FaceRecognitionResult.kt  # FaceRecognitionResult, FaceResult, BoundingBox, FaceMatch
│   │   ├── InviteCode.kt       # InviteCode, InviteCodeCreate
│   │   └── Settings.kt         # AppSettings, AppSettingsUpdate
│   └── repository/             # Интерфейсы репозиториев
│       ├── AuthRepository.kt
│       ├── EmployeeRepository.kt
│       ├── FaceRecognitionRepository.kt
│       ├── InviteCodeRepository.kt
│       └── SettingsRepository.kt
├── data/
│   ├── local/                  # Локальное хранение настроек
│   │   └── LocalSettingsStorage.kt  # java.util.prefs.Preferences (тема, API URL)
│   ├── dto/                    # Сериализуемые DTO для API
│   │   ├── AuthDto.kt          # LoginRequestDto, RegisterRequestDto, TokenResponseDto, AdminResponseDto
│   │   ├── EmployeeDto.kt      # EmployeeCreateDto, EmployeeUpdateDto, EmployeeResponseDto, EmployeeStatsDto
│   │   ├── FaceRecognitionDto.kt  # FaceRecognitionResponseDto, FaceResultDto, FaceMatchDto
│   │   ├── InviteCodeDto.kt    # InviteCodeCreateDto, InviteCodeResponseDto
│   │   └── SettingsDto.kt     # SettingsResponseDto, SettingsUpdateDto
│   ├── mapper/                 # Функции-расширения DTO ↔ Domain
│   │   ├── AuthMappers.kt
│   │   ├── EmployeeMappers.kt
│   │   ├── FaceRecognitionMappers.kt
│   │   ├── InviteCodeMappers.kt
│   │   └── SettingsMappers.kt
│   ├── remote/                 # Сетевой слой
│   │   ├── ApiClient.kt        # Конфигурация Ktor HttpClient, управление токеном
│   │   ├── ApiService.kt       # Все вызовы эндпоинтов (suspend-функции)
│   │   └── ApiException.kt     # ApiException, NetworkException
│   └── repository/             # Реализации репозиториев
│       ├── AuthRepositoryImpl.kt
│       ├── EmployeeRepositoryImpl.kt
│       ├── FaceRecognitionRepositoryImpl.kt
│       ├── InviteCodeRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── api/
│   └── ApiService.kt           # Тестовый/демо-клиент (dummyjson.com)
├── screen/                     # Voyager Screen'ы (навигация)
│   ├── HomeScreen.kt           # Главный экран администратора
│   ├── LoginAdminScreen.kt    # Экран входа администратора
│   ├── RegisterAdminScreen.kt # Экран регистрации администратора
│   └── AdminStubScreen.kt     # Заглушка администратора
├── presentation/
│   └── SettingsState.kt        # Состояние UI настроек (тема, API URL, статус подключения)
├── ui/
│   ├── buttons/
│   │   └── LoginButton.kt      # Кнопка входа
│   ├── textfields/
│   │   ├── LoginTextField.kt   # Текстовое поле логина
│   │   └── PasswordTextField.kt # Текстовое поле пароля
│   ├── icons/
│   │   ├── SettingsIcon.kt     # Иконка настроек
│   │   └── VisibilityIcon.kt   # Иконка видимости пароля
│   ├── settings/
│   │   └── SettingsOverlay.kt  # Диалог настроек
│   └── theme/
│       ├── Color.kt            # Палитры цветов (светлая / тёмная)
│       └── Theme.kt             # KotlinAppTheme composable
└── util/
    └── ErrorMapper.kt          # mapException() — ApiException/NetworkException → сообщения на русском
```

---

## 4. Соглашения

### Именование

| Категория | Соглашение | Пример |
|-----------|-----------|--------|
| Доменные модели | `camelCase`, plain data classes | `Employee`, `AuthResult` |
| Поля DTO | `snake_case` (совпадает с JSON сервера) | `employee_id`, `created_at` |
| Доменные поля | `camelCase` | `employeeId`, `createdAt` |
| Классы DTO | Суффикс `Dto` | `EmployeeResponseDto` |
| Интерфейсы репозиториев | Суффикс `Repository` | `AuthRepository` |
| Реализации репозиториев | Суффикс `RepositoryImpl` | `AuthRepositoryImpl` |
| Мапперы | Функции-расширения `toDto()` / `toDomain()` | `EmployeeResponseDto.toDomain()` |
| Экраны навигации | Суффикс `Screen`, реализуют `cafe.adriel.voyager.core.screen.Screen` | `HomeScreen`, `LoginAdminScreen` |
| Состояния UI | Суффикс `State` | `SettingsState` |
| Оверлеи | Суффикс `Overlay` | `SettingsOverlay` |

### Архитектурные правила

- **Слой domain** не имеет внешних зависимостей — только Kotlin stdlib
- **Слой data** реализует интерфейсы репозиториев domain; зависит от доменных моделей
- **DTO** помечаются аннотацией `@Serializable` из kotlinx-serialization
- **Мапперы** — функции-расширения Kotlin над классами DTO/domain
- **Репозитории** принимают и возвращают только доменные модели; преобразование DTO ↔ Domain происходит внутри `*RepositoryImpl`
- **ApiService** (data.remote) возвращает «сырые» DTO; не касается доменных моделей
- **ApiClient** управляет JWT-токеном и добавляет `Authorization: Bearer <token>` ко всем аутентифицированным запросам
- **ServiceLocator** — singleton-объект, создающий ApiClient, ApiService и все репозитории; URL сервера инициализируется из LocalSettingsStorage; метод `updateBaseUrl()` пересоздаёт HTTP-соединение
- **LocalSettingsStorage** — singleton-объект поверх `java.util.prefs.Preferences`, хранит тему и API URL (реестр Windows / dot-файлы Linux/macOS)
- **Voyager** — навигация через `Screen`-классы; переходы через `LocalNavigator.currentOrThrow`
- **ErrorMapper** — `mapException()` переводит ApiException/NetworkException в пользовательские сообщения на русском

### Тема

- Material3 light/dark с кастомными цветовыми палитрами в `ui/theme/Color.kt`
- `KotlinAppTheme(darkTheme)` composable в `ui/theme/Theme.kt` оборачивает контент в `MaterialTheme + Surface`
- Переключатель темы хранится в `SettingsState.isDarkTheme`, значение загружается из `LocalSettingsStorage`

### Сериализация

- JSON-сериализация через `kotlinx-serialization`
- Конфигурация `Json`: `ignoreUnknownKeys = true`, `encodeDefaults = false`, `explicitNulls = false`, `isLenient = true`
- Content-negotiation через Ktor-плагин `ContentNegotiation` с `kotlinx(Json { ... })`

### Сеть

- Базовый URL: `http://localhost:8000` (настраивается через SettingsState → LocalSettingsStorage → ServiceLocator)
- Движок: Ktor CIO
- Логирование: `LogLevel.INFO` (через SLF4J-backend)
- Multipart-загрузки: `MultiPartFormDataContent` для регистрации сотрудника (фото) и распознавания лиц (изображение)

---

## 5. Справочник по API сервера

**Базовый URL:** `http://localhost:8000`
**Аутентификация:** JWT Bearer-токен в заголовке `Authorization`

### Аутентификация

| Метод | Эндпоинт | Auth | Тело запроса | Ответ |
|-------|----------|------|-------------|-------|
| POST | `/api/v1/admins/login` | Нет | `{username, password}` | `{access_token, token_type}` |
| POST | `/api/v1/admins/register` | Нет | `{username, email, password, invite_code}` | `{id, username, email, created_at}` |
| GET | `/api/v1/admins/me` | Да | — | `{id, username, email, created_at}` |

### Инвайт-коды

| Метод | Эндпоинт | Auth | Тело запроса | Ответ |
|-------|----------|------|-------------|-------|
| POST | `/api/v1/admin/invites` | Да | `{expires_hours}` | `{id, code, created_by, expires_at, is_used, created_at}` |
| GET | `/api/v1/admin/invites` | Да | — | `[{InviteCodeResponse}]` |
| DELETE | `/api/v1/admin/invites/{id}` | Да | — | 204 No Content |

### Сотрудники

| Метод | Эндпоинт | Auth | Тело запроса | Ответ |
|-------|----------|------|-------------|-------|
| POST | `/api/v1/employees/register` | Да | Multipart: поля формы + файл изображения | `{EmployeeResponse}` |
| GET | `/api/v1/employees?skip=0&limit=100` | Да | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/search?q=` | Да | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/stats` | Да | — | `{total, active, inactive}` |
| PUT | `/api/v1/employees/{id}` | Да | `{EmployeeUpdate}` | `{EmployeeResponse}` |
| DELETE | `/api/v1/employees/{id}` | Да | — | 204 No Content |

### Распознавание лиц

| Метод | Эндпоинт | Auth | Тело запроса | Ответ |
|-------|----------|------|-------------|-------|
| POST | `/api/v1/faces/recognize` | Да | Multipart: файл изображения | `{faces_detected, results[{bbox, matches[{id, username, similarity}]}]}` |

### Настройки

| Метод | Эндпоинт | Auth | Тело запроса | Ответ |
|-------|----------|------|-------------|-------|
| GET | `/api/v1/settings` | Да | — | `{SettingsResponse}` |
| PUT | `/api/v1/settings` | Да | `{SettingsUpdate}` | `{SettingsResponse}` |
| POST | `/api/v1/settings/backup` | Да | — | 200 OK |

### Здоровье сервера

| Метод | Эндпоинт | Auth | Ответ |
|-------|----------|------|-------|
| GET | `/health` | Нет | `{status: "ok"}` |

### Модели сервера (Pydantic-валидация)

| Модель | Поля | Валидация |
|--------|------|-----------|
| AdminRegister | username, email, password, invite_code | username 3-50 символов, password 6-128 символов, invite_code 8-32 символов |
| AdminLogin | username, password | — |
| AdminResponse | id, username, email, created_at | — |
| TokenResponse | access_token, token_type="bearer" | — |
| EmployeeCreate | employee_id, username, email, phone, department, position, location, hire_date, is_active, access_enabled | + файл изображения |
| EmployeeResponse | id, employee_id, username, email, phone, department, position, location, hire_date, is_active, access_enabled, photo_path, created_at | — |
| EmployeeUpdate | все поля опциональны | — |
| EmployeeStats | total, active, inactive | — |
| SettingsResponse | id, theme, fullscreen, camera_resolution, camera_fps, sound_notifications, access_notifications, match_threshold, two_factor_enabled, auto_backup, backend_url, connection_timeout | — |
| InviteCodeCreate | expires_hours (по умолчанию 24) | — |
| InviteCodeResponse | id, code, created_by, expires_at, is_used, created_at | — |

---

## 6. Сборка и запуск

```bash
# Запуск десктопного приложения
./gradlew run

# Создание дистрибутива
./gradlew packageDistributable

# Сборка MSI-установщика (Windows)
./gradlew packageMsi

# Запуск тестов
./gradlew allTests
```

**Точка входа:** `com.example.kotlinapp.MainKt`
**Главная функция:** `main()` в `main.kt` → `Window` → `App()` composable

---

## Статус

- **domain/** — Модели ✅, Интерфейсы репозиториев ✅
- **data/dto/** — Все DTO ✅
- **data/mapper/** — Все мапперы ✅
- **data/local/** — LocalSettingsStorage ✅
- **data/remote/** — ApiClient ✅, ApiService ✅, ApiException ✅
- **data/repository/** — Все 5 реализаций ✅
- **api/** — Демо-клиент (dummyjson.com) ✅
- **screen/** — 4 Voyager Screen'а ✅
- **presentation/** — SettingsState ✅
- **ui/** — Кнопки, текстовые поля, иконки, настройки, тема ✅
- **util/** — ErrorMapper ✅