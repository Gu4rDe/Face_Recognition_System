## Context

Фронтенд KotlinApp — десктопное CMP-приложение (JVM-таргет) для системы распознавания лиц. Текущая архитектура:
- **Навигация**: Voyager 1.1.0-beta01 (5 Screen-классов, `LocalNavigator.currentOrThrow`)
- **DI**: ручной singleton `ServiceLocator` — создаёт ApiClient, ApiService, 5 репозиториев
- **Состояние**: `SettingsState` (plain class с `mutableStateOf`), остальные экраны — `remember { mutableStateOf }` в composable-функциях
- **WebcamService**: `object`-синглтон без интерфейса
- **Удалённый код**: `api/ApiService.kt` (dummyjson), `HomeScreen` (экран-заглушка с одной кнопкой)
- **Тесты**: отсутствуют

Проект использует CMP 1.10.3, Material3 1.10.0-alpha05, Ktor 3.0.3, lifecycle 2.10.0. Все источники находятся в `jvmMain`.

## Goals / Non-Goals

**Goals:**
- Заменить Voyager на Compose Navigation 2.8.x с `NavHost`/`NavHostController`
- Заменить ServiceLocator на Koin 4.x с модульной структурой
- Внедрить ViewModel для каждого экрана с `StateFlow<UiState>`
- Конвертировать `WebcamService` из `object` в `class` с Koin-инжекцией
- Реализовать пошаговую регистрацию с 3–5 фото
- Добавить Kotest + MockK тестовое покрытие
- Удалить мусорный код (demo ApiService, HomeScreen, MenuItem)

**Non-Goals:**
- Изменение версии Material3 (остаёмся на 1.10.0-alpha05)
- Изменение domain-слоя (модели, интерфейсы репозиториев)
- Изменение data-слоя (DTO, мапперы) — за исключением добавления `registerWithPhotos()`
- Миграция на мультиплатформенные source sets (оставляем `jvmMain`)
- Локализация (strings остаются захардкожены на русском)
- Изменение сетевого слоя (Ktor, ApiClient) — кроме мутабельного `baseUrl`

## Decisions

### 1. Compose Navigation вместо Voyager

**Выбор**: `org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10`

**Альтернативы**:
- Decompose — отдельная библиотека, другая парадигма (не NavHost)
- Остаться на Voyager — beta01 без обновлений, нет поддержки

**Обоснование**: Compose Navigation — официальная библиотека CMP, совместимая с lifecycle-viewmodel-compose. Итерации 1+2 из v2 плана объединены — Voyager удаляется и заменяется за один проход, чтобы проект всегда собирался.

### 2. Навигация внутри MainScreen — `mutableStateOf` вместо вложенного NavHost

**Выбор**: `remember { mutableStateOf(MainSection) }` вместо вложенного `NavHost` внутри `MainScreen`.

**Обоснование**: Desktop-приложение с sidebar не требует вложенной навигации — разделы переключаются состоянием, а не маршрутами. Это проще и предсказуемее.

### 3. Koin 4.x как DI-фреймворк

**Выбор**: Koin 4.0.0 (koin-core, koin-compose, koin-compose-viewmodel)

**Альтернативы**:
- Dagger/Kodein — сложнее для CMP, нет нативной поддержки ViewModel
- Оставить ServiceLocator — нетестопригодно, нет масштабируемости

**Обоснование**: Koin — стандарт для CMP, лёгкий, поддерживает `viewModel { }` для создания ViewModel с инжекцией. `ApiClient` остаётся `single { }` с мутабельными полями (`baseUrl`, `token`).

### 4. WebcamService — class вместо object

**Выбор**: Конвертировать `WebcamService` из `object` в `class`, инжектировать через Koin `single { WebcamService() }`.

**Обоснование**: Позволяет мокать `WebcamService` в тестах. Синглтон-поведение сохраняется через Koin scope.

### 5. Стартовый маршрут — Login (не Home)

**Выбор**: `startDestination = AppScreen.Login.route`, `HomeScreen` удаляется.

**Обоснование**: HomeScreen содержал только кнопку «Вход», что лишний шаг. Навигация: Login → Main после успешной аутентификации, Register/PasswordRecovery — с Login.

### 6. ViewModel на каждый экран

**Выбор**: 7 ViewModels с `StateFlow<UiState>` — Login, Register, PasswordRecovery, Dashboard, Employee, FaceRecognition, Settings.

**Обоснование**: Убирает бизнес-логику из composables, делает состояние тестируемым. Все ViewModels инжектируются через `koinViewModel()`.

### 7. Мультифото-регистрация

**Выбор**: `PhotoRegistrationUiState` с 5 шагами (3 обязательных, 2 необязательных), `PhotoCaptureDialog` как event-driven composable.

**Обоснование**: Сервер готов принять несколько фото через multipart. Пошаговый UI улучшает качество распознавания.

### 8. Тестирование — Kotest + MockK

**Выбор**: Kotest 5.9.1 (`FunSpec`) + MockK 1.13.12

**Обоснование**: Kotest — идиоматичный Kotlin-фреймворк с `FunSpec`, MockK — стандарт для мокирования Kotlin-классов. `useJUnitPlatform()` в `build.gradle.kts`.

## Risks / Trade-offs

- **[Compose Navigation alpha]** → Mitigation: CMP team активно развивает; через `libs.versions.toml` можно быстро обновить. Проверить `./gradlew compileKotlin` после добавления.
- **[Logout без очистки токена между Итерациями 1–2]** → Mitigation: явно документировать как известный промежуточный дефект. Исключить ручное тестирование logout до завершения Итерации 3.
- **[Koin ViewModel lifecycle на JVM Desktop]** → Mitigation: lifecycle-viewmodel-compose 2.10.0 уже в зависимостях. `koinViewModel()` создаёт ViewModel в scope Composable. Проверить, что SettingsViewModel не пересоздаётся при навигации (проверка: `remember` в `App()`).
- **[Мультифото API]** → Mitigation: `registerWithPhotos()` — новый метод в `EmployeeRepository`. Если бэкенд не поддерживает multipart с 3–5 файлами, ограничиться текущим API (1 фото) и реализовать UI-прогресс как stub.
- **[ApiClient.baseUrl мутабельность]** → Mitigation: setter вызывает `rebuildClient()`. Проверить thread-safety при одновременном доступе.

## Migration Plan

Пошагово, по итерациям (подробнее в tasks.md):

1. **Итерация 0**: Создать ветку, удалить мусор (api/ApiService.kt, HomeScreen)
2. **Итерация 1**: Обновить зависимости (libs.versions.toml, build.gradle.kts), создать навигацию (AppScreen, AppNavGraph), переписать экраны без Voyager, удалить MenuItem
3. **Итерация 2**: Создать Koin-модули (di/AppModule.kt), конвертировать WebcamService, подключить Koin в main.kt, заменить все ServiceLocator-вызовы, удалить ServiceLocator.kt
4. **Итерация 3**: Создать 7 ViewModels, переписать экраны на koinViewModel() + StateFlow, интегрировать SettingsOverlay, logout через ViewModel
5. **Итерация 4**: Рефакторинг PhotoCaptureDialog, PhotoRegistrationUiState, EmployeeViewModel расширение, registerWithPhotos() в EmployeeRepository
6. **Итерация 5**: Kotest + MockK тесты для ViewModels, ErrorMapper, PhotoRegistrationUiState
7. **Итерация 6**: Финальная проверка — сборка, ручное тестирование, чеклист

**Rollback**: Каждая итерация — отдельный коммит. При критической ошибке — `git revert` на предыдущий коммит.

## Open Questions

1. Поддерживает ли бэкенд multipart-загрузку 3–5 фото? Если нет — `registerWithPhotos()` будет stub.
2. Нужно ли сохранять `PasswordRecoveryScreen` как отдельный маршрут, или можно заменить на диалог?
3. Требуется ли анимация переходов между экранами (Compose Navigation поддерживает)?