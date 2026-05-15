## Why

Текущий фронтенд использует устаревший навигационный фреймворк Voyager (1.1.0-beta01), ручное внедрение зависимостей через singleton-объект `ServiceLocator`, и разбросанное состояние UI (`mutableStateOf` в composables вместо ViewModel). Это делает код трудным для тестирования, масштабирования и сопровождения. Кроме того, демо-файл `api/ApiService.kt` (dummyjson) и `HomeScreen` (экран с одной кнопкой) — мусор, усложняющий навигацию. Регистрация сотрудника поддерживает только 1 фото, тогда как сервер способен принять несколько.

Миграция на Compose Navigation + Koin + ViewModel приведёт архитектуру к стандарту CMP, упростит тестирование и позволит реализовать пошаговую регистрацию с 3–5 фотографиями.

## What Changes

- **BREAKING**: Voyager навигация заменяется на Compose Navigation 2.8.x (`NavHost`, `NavHostController`, sealed class маршрутов)
- **BREAKING**: ServiceLocator заменяется на Koin 4.x DI (модули: network, infrastructure, repository, viewModel)
- **BREAKING**: Все экраны переписаны с Voyager `Screen` на `@Composable fun` с `NavHostController`
- `HomeScreen` удаляется — стартовый маршрут `Login`
- `MenuItem.kt` удаляется — заменяется на `MainSection` enum
- `api/ApiService.kt` (демо-клиент dummyjson) удаляется
- `WebcamService` конвертируется из `object` в `class` + Koin `single`
- `SettingsState` заменяется на `SettingsViewModel` с `StateFlow`
- Добавляются 7 ViewModels: `LoginViewModel`, `RegisterViewModel`, `PasswordRecoveryViewModel`, `DashboardViewModel`, `EmployeeViewModel`, `FaceRecognitionViewModel`, `SettingsViewModel`
- `PhotoCaptureDialog` рефакторится для пошагового UI (3 обязательных + 2 необязательных фото)
- `EmployeeRepository` расширяется методом `registerWithPhotos()` для multipart-загрузки 3–5 фото
- Навигация внутри `MainScreen` через `remember { mutableStateOf(MainSection) }` вместо вложенного NavHost
- Logout очищает весь backstack: `popUpTo(0) { inclusive = true }`
- Тестирование: JUnit 4 → Kotest + MockK

## Capabilities

### New Capabilities
- `compose-navigation`: Навигация на Compose Navigation (NavHost, маршруты, переходы, backstack management)
- `koin-di`: Внедрение зависимостей через Koin (модули, инжекция ViewModels, singleton-компоненты)
- `viewmodels`: ViewModel для каждого экрана с StateFlow, управление жизненным циклом через Koin
- `multi-photo-registration`: Пошаговая регистрация сотрудника с 3–5 фотографиями, прогресс-UI, пропуск необязательных шагов
- `unit-testing`: Kotest + MockK покрытие ViewModels, ErrorMapper, PhotoRegistrationUiState

### Modified Capabilities
- `main-navigation`: Переход с Voyager на Compose Navigation, удаление HomeScreen, замена MenuItem на MainSection enum
- `employee-management`: Добавление registerWithPhotos(), ViewModel для списка/поиска/удаления сотрудников
- `dashboard`: DashboardViewModel для статистики и проверки сервера
- `face-recognition-ui`: FaceRecognitionViewModel, инжекция WebcamService
- `face-recognition-settings`: SettingsViewModel вместо SettingsState, интеграция с Koin

## Impact

- **Зависимости**: `gradle/libs.versions.toml` — удалить Voyager, добавить Compose Navigation, Koin, Kotest, MockK
- **Сборка**: `composeApp/build.gradle.kts` — обновить зависимости, добавить `useJUnitPlatform()`
- **Навигация**: Все файлы в `screen/` переписаны (удаление Voyager, добавление `navController`), новый пакет `navigation/`
- **DI**: Новый пакет `di/` с `AppModule.kt`, удаление `ServiceLocator.kt`
- **ViewModels**: Новый пакет `viewmodel/` с 7 классами + UiState data classes
- **Точки входа**: `main.kt` — `startKoin`, `App.kt` — `rememberNavController` + Koin ViewModel
- **删除**: `HomeScreen.kt`, `MenuItem.kt`, `api/ApiService.kt`, `ServiceLocator.kt`, `presentation/SettingsState.kt`
- **Тесты**: Новый source set `jvmTest/` с Kotest-спецификациями
- **Риски**: Compose Navigation 2.8.0-alpha10 совместимость с CMP 1.10.3; Koin ViewModel lifecycle на JVM Desktop