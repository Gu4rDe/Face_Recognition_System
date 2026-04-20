# KotlinApp

Десктопный клиент для системы распознавания лиц **Miit_FaceDetect**. Построен на **Compose Multiplatform** (JVM-таргет) с **Material3**, предоставляет UI для аутентификации администратора, управления сотрудниками, распознавания лиц и настроек приложения.

## Технологический стек

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
| Логирование | SLF4J | 2.0.16 |
| Тестирование | JUnit | 4.13.2 |
| Hot Reload | Compose Hot Reload | 1.0.0 |
| Сборка | Gradle (Kotlin Multiplatform) | — |
| Десктоп-упаковка | Compose Desktop (Dmg, Msi, Deb) | — |

## Архитектура

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

**Вспомогательные компоненты:**
- **ServiceLocator** — singleton DI-контейнер, создаёт и предоставляет ApiClient, ApiService, все репозитории
- **util/ErrorMapper** — преобразует ApiException/NetworkException в пользовательские сообщения

## Структура проекта

```
com.example.kotlinapp
├── main.kt                    # Точка входа (Window)
├── App.kt                     # Корневой composable
├── ServiceLocator.kt          # Singleton DI-контейнер
├── domain/
│   ├── model/                  # Бизнесовые классы данных
│   │   ├── Admin.kt
│   │   ├── AuthResult.kt
│   │   ├── Employee.kt
│   │   ├── FaceRecognitionResult.kt
│   │   ├── InviteCode.kt
│   │   └── Settings.kt
│   └── repository/             # Интерфейсы репозиториев
│       ├── AuthRepository.kt
│       ├── EmployeeRepository.kt
│       ├── FaceRecognitionRepository.kt
│       ├── InviteCodeRepository.kt
│       └── SettingsRepository.kt
├── data/
│   ├── local/                  # Локальное хранение настроек
│   │   └── LocalSettingsStorage.kt
│   ├── dto/                    # Сериализуемые DTO для API
│   │   ├── AuthDto.kt
│   │   ├── EmployeeDto.kt
│   │   ├── FaceRecognitionDto.kt
│   │   ├── InviteCodeDto.kt
│   │   └── SettingsDto.kt
│   ├── mapper/                 # Функции-расширения DTO ↔ Domain
│   │   ├── AuthMappers.kt
│   │   ├── EmployeeMappers.kt
│   │   ├── FaceRecognitionMappers.kt
│   │   ├── InviteCodeMappers.kt
│   │   └── SettingsMappers.kt
│   ├── remote/                 # Сетевой слой
│   │   ├── ApiClient.kt
│   │   ├── ApiService.kt
│   │   └── ApiException.kt
│   └── repository/             # Реализации репозиториев
│       ├── AuthRepositoryImpl.kt
│       ├── EmployeeRepositoryImpl.kt
│       ├── FaceRecognitionRepositoryImpl.kt
│       ├── InviteCodeRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── api/
│   └── ApiService.kt           # Тестовый/демо-клиент (dummyjson.com)
├── screen/                     # Voyager Screen'ы
│   ├── HomeScreen.kt
│   ├── LoginAdminScreen.kt
│   ├── RegisterAdminScreen.kt
│   └── AdminStubScreen.kt
├── presentation/
│   └── SettingsState.kt
├── ui/
│   ├── buttons/
│   │   └── LoginButton.kt
│   ├── textfields/
│   │   ├── LoginTextField.kt
│   │   └── PasswordTextField.kt
│   ├── icons/
│   │   ├── SettingsIcon.kt
│   │   └── VisibilityIcon.kt
│   ├── settings/
│   │   └── SettingsOverlay.kt
│   └── theme/
│       ├── Color.kt
│       └── Theme.kt
└── util/
    └── ErrorMapper.kt
```

## Функциональность

- **Аутентификация администратора** — вход, регистрация с инвайт-кодами, управление JWT Bearer-токеном
- **Управление сотрудниками** — CRUD-операции, поиск, захват фото через веб-камеру, статистика сотрудников
- **Распознавание лиц** — загрузка изображения или захват с веб-камеры, оверлей bounding box с цветовой индикацией уверенности, результаты сопоставления с данными сотрудников
- **Настройки** — переключение темы (светлая/тёмная), настраиваемый базовый URL API, параметры распознавания лиц (порог совпадения, разрешение камеры, FPS), постоянное хранение через `java.util.prefs.Preferences`
- **Навигация** — навигация на основе Voyager с переключением секций через composable-функции (Dashboard, Employees, Face Recognition)

## Соглашения

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
| Экраны навигации | Суффикс `Screen` | `HomeScreen`, `LoginAdminScreen` |
| Состояния UI | Суффикс `State` | `SettingsState` |
| Оверлеи | Суффикс `Overlay` | `SettingsOverlay` |

### Сериализация

- JSON через `kotlinx-serialization`
- Конфигурация: `ignoreUnknownKeys = true`, `encodeDefaults = false`, `explicitNulls = false`, `isLenient = true`
- Content negotiation через плагин Ktor `ContentNegotiation`

### Сеть

- Базовый URL: `http://localhost:8000` (настраивается во время выполнения)
- Движок: Ktor CIO
- Аутентификация: JWT Bearer-токен в заголовке `Authorization`
- Multipart-загрузки для фото сотрудников и изображений распознавания лиц

## Сборка и запуск

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

## Справочник по API сервера

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

## Статус реализации

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
