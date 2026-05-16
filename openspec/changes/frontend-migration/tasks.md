## 0. Подготовка

- [x] 0.1 Создать ветку `feature/frontend-migration`
- [x] 0.2 Удалить `composeApp/src/jvmMain/kotlin/com/example/kotlinapp/api/ApiService.kt` (демо-клиент dummyjson)
- [x] 0.3 Удалить `composeApp/src/jvmMain/kotlin/com/example/kotlinapp/screen/HomeScreen.kt`
- [x] 0.4 Зафиксировать baseline: Login/Register admin, Password recovery, Employee list/search/delete, Employee registration (1 фото), Face recognition, Settings

## 1. Зависимости + Навигация

- [x] 1.1 Обновить `gradle/libs.versions.toml`: удалить Voyager, добавить compose-navigation 2.8.0-alpha10, koin 4.0.0, mockk 1.13.12, kotest 5.9.1
- [x] 1.2 Обновить `composeApp/build.gradle.kts`: заменить voyager-зависимости на compose-navigation, koin-core, koin-compose, koin-compose-viewmodel; добавить kotest-runner, kotest-assertions, mockk в testImplementation; добавить `tasks.withType<Test> { useJUnitPlatform() }`
- [x] 1.3 Создать `navigation/AppScreen.kt` — sealed class с маршрутами: Login, Register, PasswordRecovery, Main({section})
- [x] 1.4 Создать `navigation/AppNavGraph.kt` — @Composable fun с NavHost и 4 composable-маршрутами
- [x] 1.5 Обновить `App.kt` — заменить Voyager Navigator на rememberNavController + AppNavGraph(navController)
- [x] 1.6 Переписать `LoginAdminScreen.kt` → `LoginScreen(navController: NavHostController)` — удалить Voyager, добавить навигацию через navController
- [x] 1.7 Переписать `RegisterAdminScreen.kt` → `RegisterScreen(navController: NavHostController)`
- [x] 1.8 Переписать `PasswordRecoveryScreen.kt` → `PasswordRecoveryScreen(navController: NavHostController)`
- [x] 1.9 Переписать `MainScreen.kt` — добавить navController, initialSection, var currentSection через remember { mutableStateOf(...) }, MainSection enum вместо MenuItem
- [x] 1.10 Переписать `DashboardScreen.kt` → `DashboardContent()` — внутренний composable без Voyager
- [x] 1.11 Переписать `EmployeeScreen.kt` → `EmployeeContent()` — внутренний composable без Voyager
- [x] 1.12 Переписать `FaceRecognitionScreen.kt` → `FaceRecognitionContent()` — внутренний composable без Voyager
- [x] 1.13 Удалить `screen/MenuItem.kt` (заменён на MainSection enum)
- [x] 1.14 Удалить все `import cafe.adriel.voyager.*` и `LocalNavigator.currentOrThrow`
- [x] 1.15 Реализовать logout-навигацию: `navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }` (без setToken(null) — будет в Итерации 3)
- [x] 1.16 Проверить: `./gradlew compileKotlin` — без ошибок

## 2. DI: ServiceLocator → Koin

- [x] 2.1 Конвертировать `WebcamService` из `object` в `class` — убрать `object`, добавить `class WebcamService` с теми же методами
- [x] 2.2 Создать `di/AppModule.kt` с четырьмя модулями: networkModule, infrastructureModule, repositoryModule, viewModelModule
- [x] 2.3 Обновить `main.kt` — добавить `startKoin { modules(networkModule, infrastructureModule, repositoryModule, viewModelModule) }` перед Window
- [ ] 2.4 Заменить все вызовы `ServiceLocator.employeeRepository` на `koinViewModel<EmployeeViewModel>()` в DashboardContent
- [ ] 2.5 Заменить все вызовы `ServiceLocator.employeeRepository` / `ServiceLocator.faceRecognitionRepository` на ViewModel в EmployeeContent и FaceRecognitionContent
- [ ] 2.6 Заменить `ServiceLocator.authRepository` на ViewModel в LoginScreen, RegisterScreen, PasswordRecoveryScreen
- [ ] 2.7 Заменить `SettingsState` прямой доступ на `SettingsViewModel` через `koinViewModel()` во всех экранах
- [x] 2.8 Обновить `ApiClient` — убедиться, что `baseUrl` setter вызывает `rebuildClient()`, ApiClient инжектируется как `single` в Koin (уже реализовано)
- [x] 2.9 Удалить `ServiceLocator.kt`
- [x] 2.10 Удалить `presentation/SettingsState.kt`
- [x] 2.11 Проверить: `./gradlew compileKotlin` — без ошибок

## 3. ViewModels

- [x] 3.1 Создать `viewmodel/LoginViewModel.kt` с LoginUiState и методом login()
- [x] 3.2 Создать `viewmodel/RegisterViewModel.kt` с RegisterUiState и методом register()
- [x] 3.3 Создать `viewmodel/PasswordRecoveryViewModel.kt` с PasswordRecoveryUiState и методом resetPassword()
- [x] 3.4 Создать `viewmodel/DashboardViewModel.kt` с DashboardUiState, loadStats(), checkServer()
- [x] 3.5 Создать `viewmodel/EmployeeViewModel.kt` с EmployeeListUiState, loadEmployees(), searchEmployees(), deleteEmployee()
- [x] 3.6 Создать `viewmodel/FaceRecognitionViewModel.kt` с FaceRecognitionUiState, recognize()
- [x] 3.7 Создать `viewmodel/SettingsViewModel.kt` с SettingsUiState, loadSettings(), applySettings(), toggleTheme(), checkServerConnection()
- [x] 3.8 Переписать `LoginScreen` — использовать `koinViewModel<LoginViewModel>()`, `uiState.collectAsState()`, `LaunchedEffect(uiState.isSuccess)` для навигации
- [x] 3.9 Переписать `RegisterScreen` — использовать `koinViewModel<RegisterViewModel>()`
- [x] 3.10 Переписать `PasswordRecoveryScreen` — использовать `koinViewModel<PasswordRecoveryViewModel>()`
- [x] 3.11 Переписать `DashboardContent` — использовать `koinViewModel<DashboardViewModel>()`
- [x] 3.12 Переписать `EmployeeContent` — использовать `koinViewModel<EmployeeViewModel>()`
- [x] 3.13 Переписать `FaceRecognitionContent` — использовать `koinViewModel<FaceRecognitionViewModel>()`
- [x] 3.14 Переписать `SettingsOverlay` — принимать `SettingsViewModel` вместо `SettingsState`, использовать `uiState.collectAsState()`
- [x] 3.15 Интегрировать `SettingsOverlay` в `App()` — `val settingsViewModel: SettingsViewModel = koinViewModel()`, передать в `KotlinAppTheme(darkTheme = settingsState.isDarkTheme)` и `SettingsOverlay(settingsViewModel)`
- [x] 3.16 Реализовать logout через ViewModel: добавить `logout()` в LoginViewModel (или создать AuthViewModel), вызывать `authRepository.setToken(null)` + `navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }`
- [x] 3.17 Перенести все вызовы `mapException()` из Composable-файлов в ViewModel
- [x] 3.18 Проверить: `./gradlew compileKotlin` — без ошибок

## 4. Мультифото регистрация

- [x] 4.1 Создать `viewmodel/PhotoRegistrationUiState.kt` — data class с capturedPhotos, currentStep, totalSteps, instruction, isUploading, canUpload, isOptionalStep
- [x] 4.2 Добавить `_photoState` и методы в `EmployeeViewModel`: onPhotoCapture(), onSkipStep(), resetPhotoState(), uploadPhotos()
- [x] 4.3 Рефакторинг `PhotoCaptureDialog` — сделать stateless composable с параметрами: isOpen, currentStep, totalSteps, instruction, capturedPhotos, onCapture, onSkip, onUpload, onDismiss
- [x] 4.4 Добавить `registerWithPhotos(data: EmployeeCreate, photos: List<ByteArray>): Result<Employee>` в `EmployeeRepository` и `EmployeeRepositoryImpl`
- [x] 4.5 Создать STEP_INSTRUCTIONS — список из 5 инструкций на русском для каждого шага
- [x] 4.6 Интегрировать пошаговый UI в EmployeeContent — отрисовка прогресса (шаг X из Y), кнопки «Захватить» / «Пропустить» / «Загрузить»
- [x] 4.7 Проверить: `./gradlew compileKotlin` — без ошибок

## 5. Тесты

- [x] 5.1 Создать source set `jvmTest/kotlin/com/example/kotlinapp/` и настроить в `build.gradle.kts`
- [x] 5.2 Создать `viewmodel/LoginViewModelTest.kt` — FunSpec с тестами: success, 401, network error
- [x] 5.3 Создать `viewmodel/RegisterViewModelTest.kt` — FunSpec с тестами: success, invalid invite code, duplicate
- [x] 5.4 Создать `viewmodel/PasswordRecoveryViewModelTest.kt` — FunSpec с тестами: success, invalid invite code
- [x] 5.5 Создать `viewmodel/DashboardViewModelTest.kt` — FunSpec с тестами: loadStats, checkServer, errors
- [x] 5.6 Создать `viewmodel/EmployeeViewModelTest.kt` — FunSpec с тестами: loadAll, search, delete, onCapture(), onSkipStep(), canUpload
- [x] 5.7 Создать `viewmodel/FaceRecognitionViewModelTest.kt` — FunSpec с тестами: recognize(), error handling
- [x] 5.8 Создать `viewmodel/SettingsViewModelTest.kt` — FunSpec с тестами: load, save, toggleTheme()
- [x] 5.9 Создать `util/ErrorMapperTest.kt` — FunSpec с тестами на все типы ApiException и NetworkException
- [x] 5.10 Создать `viewmodel/PhotoRegistrationUiStateTest.kt` — FunSpec с тестами: canUpload = false при < 3 фото, canUpload = true при >= 3, skip step logic
- [ ] 5.11 Проверить: `./gradlew allTests` — все тесты зелёные

## 6. Финальная проверка

- [x] 6.1 Убедиться, что `api/ApiService.kt` удалён
- [x] 6.2 Убедиться, что `HomeScreen.kt` удалён
- [x] 6.3 Убедиться, что `ServiceLocator.kt` удалён
- [x] 6.4 Убедиться, что `MenuItem.kt` удалён (заменён на MainSection enum)
- [x] 6.5 Убедиться, что Voyager полностью удалён из зависимостей
- [x] 6.6 Убедиться, что `mapException()` вызовов нет в Composable-файлах (только в ViewModel)
- [x] 6.7 Убедиться, что `WebcamService` конвертирован из `object` в `class`
- [x] 6.8 `./gradlew compileKotlin` — без ошибок
- [ ] 6.9 `./gradlew allTests` — зелёные
- [ ] 6.10 Ручное тестирование: Login / Logout работает (popUpTo Login inclusive)
- [ ] 6.11 Ручное тестирование: Register admin с invite code работает
- [ ] 6.12 Ручное тестирование: Password recovery работает
- [ ] 6.13 Ручное тестирование: Employee list, search, delete работает
- [ ] 6.14 Ручное тестирование: Регистрация — 3 обязательных + 2 необязательных фото
- [ ] 6.15 Ручное тестирование: Face recognition — bbox + similarity отображается
- [ ] 6.16 Ручное тестирование: Settings сохраняются между запусками (API URL меняется на лету)
- [ ] 6.17 Ручное тестирование: WebcamService не пересоздаётся при переходах
- [ ] 6.18 Ручное тестирование: Dashboard показывает статистику и статус сервера
- [ ] 6.19 `./gradlew run` — запускается без ошибок
- [ ] 6.20 `./gradlew packageDistributable` — пакет собирается