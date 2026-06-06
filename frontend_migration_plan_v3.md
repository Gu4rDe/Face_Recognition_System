# Финальный план миграции фронтенда v2
## Исправлены все недочёты предыдущей версии

---

## Изменения относительно предыдущего плана

| Проблема | Исправление |
|----------|-------------|
| Путь api/ApiService.kt | Исправлен на `jvmMain` |
| WebcamService | Добавлен в Koin как `single` |
| PhotoCaptureDialog | Описан рефакторинг |
| FormValidator | Оставляем как утилиту, без изменений |
| Voyager удалялся до замены | Теперь: сначала Итерация 2, потом удаление |
| mapException() в screens | Перенесён в ViewModel через ErrorMapper |
| PasswordRecoveryScreen | Добавлен в план |
| Material3 версия | Уточнён выбор |
| SettingsState → SettingsViewModel | Описана миграция |
| Навигация внутри MainScreen | Описана стратегия |

---

## Текущий стек → Целевой стек

| Компонент | Сейчас | После |
|-----------|--------|-------|
| Navigation | Voyager 1.1.0-beta01 | Compose Navigation 2.8.x |
| DI | ServiceLocator (singleton) | Koin 4.x |
| Material3 | 1.10.0-alpha05 | 1.3.2 stable |
| ViewModel | Только SettingsState | ViewModel на каждый экран |
| WebcamService | Создаётся вручную | Koin single |
| Регистрация фото | 1 фото | 3–5 фото + пошаговый UI |
| Тесты | JUnit 4 | Kotest + MockK |
| Мусор | api/ApiService.kt (dummyjson) | Удалён |
| Заглушки | AdminStubScreen | Удалён или реализован |

---

## Итерация 0 — Подготовка (30 минут)

### 0.1 Ветка
```bash
git checkout -b feature/frontend-migration
```

### 0.2 Удали мусор — правильный путь
```bash
# Путь jvmMain, не desktopMain
rm composeApp/src/jvmMain/kotlin/com/example/kotlinapp/api/ApiService.kt
git commit -m "remove: demo ApiService with dummyjson.com"
```

### 0.3 Реши судьбу AdminStubScreen
```bash
# Если удалять:
rm composeApp/src/jvmMain/kotlin/com/example/kotlinapp/screen/AdminStubScreen.kt
git commit -m "remove: AdminStubScreen placeholder"
```

Если планируешь управление invite-кодами и статистику — создай GitHub issue,
реализуй отдельно после основной миграции.

### 0.4 Зафикcируй baseline
```
[ ] Login работает
[ ] Register работает
[ ] Password recovery работает
[ ] Employee list, search, delete работает
[ ] Employee registration (1 фото)
[ ] Face recognition + bbox
[ ] Settings (theme, API URL, threshold)
```

---

## Итерация 1 — Стабилизация зависимостей (1 час)

### Про Material3: alpha или stable?

`1.10.0-alpha05` — экспериментальная, API может меняться.
`1.3.2` — стабильная, проверенная, все компоненты доступны.

**Рекомендация: перейти на `1.3.2`.**
Если используешь экспериментальные компоненты из alpha (например, новые варианты Card или NavigationDrawer) — проверь их наличие в 1.3.2 перед обновлением.

### 1.1 `gradle/libs.versions.toml`

```toml
[versions]
# Обновить:
compose-material3    = "1.3.2"          # было 1.10.0-alpha05

# Добавить:
compose-navigation   = "2.8.0-alpha10"
koin                 = "4.0.0"
mockk                = "1.13.12"
kotest               = "5.9.1"

# Voyager НЕ удалять на этом шаге — до готовности Итерации 2

[libraries]
# Добавить:
koin-core            = { module = "io.insert-koin:koin-core",                              version.ref = "koin" }
koin-compose         = { module = "io.insert-koin:koin-compose",                           version.ref = "koin" }
koin-viewmodel       = { module = "io.insert-koin:koin-compose-viewmodel",                 version.ref = "koin" }
compose-navigation   = { module = "org.jetbrains.androidx.navigation:navigation-compose",  version.ref = "compose-navigation" }
mockk                = { module = "io.mockk:mockk",                                        version.ref = "mockk" }
kotest-runner        = { module = "io.kotest:kotest-runner-junit5",                        version.ref = "kotest" }
kotest-assertions    = { module = "io.kotest:kotest-assertions-core",                      version.ref = "kotest" }
```

### 1.2 `composeApp/build.gradle.kts`
```kotlin
dependencies {
    // Voyager — НЕ удалять до Итерации 2
    implementation(libs.voyager.navigator)
    implementation(libs.voyager.screenmodel)

    // Добавить новые:
    implementation(libs.compose.navigation)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.viewmodel)

    // Тесты — заменить JUnit:
    // testImplementation(libs.junit)    ← убрать
    testImplementation(libs.kotest.runner)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.mockk)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

### 1.3 Проверь компиляцию
```bash
./gradlew compileKotlin
# Должно компилироваться — Voyager ещё не удалён
```

---

## Итерация 2 — Замена навигации: Voyager → Compose Navigation (3–4 часа)

Voyager удаляется **только в конце этой итерации**, когда все экраны переписаны.

### 2.1 Стратегия навигации внутри MainScreen

У приложения два уровня навигации:
- **Верхний уровень:** Login → Register / PasswordRecovery → Main
- **Внутри Main:** Sidebar → Dashboard / Employees / FaceRecognition / Settings

**Рекомендация: вложенный NavGraph.**

```
AppNavGraph (верхний):
├── login
├── register
├── password_recovery    ← новый
└── main                 ← вложенный NavHost
    ├── dashboard
    ├── employees
    ├── face_recognition
    └── settings
```

Sidebar управляет внутренним NavController, не верхним.
Это чище, чем плоские маршруты — не нужно прокидывать navController через всё дерево.

### 2.2 `navigation/AppRoutes.kt`
```kotlin
object AppRoutes {
    // Верхний уровень
    const val LOGIN            = "login"
    const val REGISTER         = "register"
    const val PASSWORD_RECOVERY = "password_recovery"
    const val MAIN             = "main"

    // Внутри Main
    const val DASHBOARD        = "dashboard"
    const val EMPLOYEES        = "employees"
    const val FACE_RECOGNITION = "face_recognition"
    const val SETTINGS         = "settings"
}
```

### 2.3 `navigation/AppNavGraph.kt`
```kotlin
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = AppRoutes.LOGIN) {
        composable(AppRoutes.LOGIN) {
            LoginScreen(navController)
        }
        composable(AppRoutes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(AppRoutes.PASSWORD_RECOVERY) {
            PasswordRecoveryScreen(navController)
        }
        composable(AppRoutes.MAIN) {
            MainScreen()   // внутри — свой NavHost для sidebar
        }
    }
}
```

### 2.4 `navigation/MainNavGraph.kt` — навигация sidebar
```kotlin
@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()
    val currentEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Row {
        AppSidebar(
            currentRoute = currentRoute,
            onNavigate = { route ->
                mainNavController.navigate(route) {
                    popUpTo(AppRoutes.DASHBOARD) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavHost(mainNavController, startDestination = AppRoutes.DASHBOARD) {
            composable(AppRoutes.DASHBOARD)        { DashboardScreen() }
            composable(AppRoutes.EMPLOYEES)        { EmployeesScreen() }
            composable(AppRoutes.FACE_RECOGNITION) { FaceRecognitionScreen() }
            composable(AppRoutes.SETTINGS)         { SettingsScreen() }
        }
    }
}
```

### 2.5 Удали Voyager — только после переписи всех экранов
```bash
# Убрать из build.gradle.kts:
# implementation(libs.voyager.navigator)
# implementation(libs.voyager.screenmodel)

# Убрать из libs.versions.toml:
# voyager = "..."

./gradlew compileKotlin  # должно компилироваться
git commit -m "refactor: replace Voyager with Compose Navigation"
```

---

## Итерация 3 — DI: ServiceLocator → Koin (2–3 часа)

### 3.1 WebcamService — добавить в Koin

WebcamService управляет камерой — это ресурс с жизненным циклом,
должен быть singleton, не создаваться заново на каждый экран.

```kotlin
// di/AppModule.kt
val serviceModule = module {
    single { WebcamService() }          // singleton — одна камера на приложение
    single { LocalSettingsStorage() }
    single { ApiClient(baseUrl = get<LocalSettingsStorage>().getBaseUrl()) }
    single { ApiService(get()) }
}

val repositoryModule = module {
    single<AuthRepository>             { AuthRepositoryImpl(get()) }
    single<EmployeeRepository>         { EmployeeRepositoryImpl(get()) }
    single<FaceRecognitionRepository>  { FaceRecognitionRepositoryImpl(get()) }
    single<InviteCodeRepository>       { InviteCodeRepositoryImpl(get()) }
    single<SettingsRepository>         { SettingsRepositoryImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { PasswordRecoveryViewModel(get()) }
    viewModel { EmployeeViewModel(get(), get()) }        // get() для WebcamService
    viewModel { FaceRecognitionViewModel(get(), get()) } // get() для WebcamService
    viewModel { SettingsViewModel(get()) }
}
```

### 3.2 `main.kt`
```kotlin
fun main() = application {
    startKoin {
        modules(serviceModule, repositoryModule, viewModelModule)
    }
    Window(onCloseRequest = ::exitApplication, title = "Face Recognition") {
        App()
    }
}
```

### 3.3 Удали ServiceLocator
```bash
rm composeApp/src/jvmMain/kotlin/com/example/kotlinapp/ServiceLocator.kt
git commit -m "refactor: replace ServiceLocator with Koin"
```

---

## Итерация 4 — ViewModel + миграция ErrorMapper (4–5 часов)

### 4.1 ErrorMapper — куда переносить

`mapException()` из screens → вызывать только внутри ViewModel.
Composable не должен знать об исключениях — только об UiState.

```kotlin
// Было (в Screen/Composable):
try {
    repository.login(...)
} catch (e: ApiException) {
    errorText = mapException(e)
}

// Стало (в ViewModel):
authRepository.login(username, password)
    .onFailure { e ->
        _uiState.update { it.copy(error = ErrorMapper.map(e)) }
    }

// Composable просто читает:
if (uiState.error != null) {
    ErrorText(uiState.error)
}
```

### 4.2 PasswordRecovery — добавить в план

```kotlin
data class PasswordRecoveryUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

class PasswordRecoveryViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState: StateFlow<PasswordRecoveryUiState> = _uiState.asStateFlow()

    fun resetPassword(username: String, inviteCode: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authRepository.resetPassword(username, inviteCode, newPassword)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = ErrorMapper.map(e)) }
                }
        }
    }
}
```

### 4.3 SettingsState → SettingsViewModel

Это самый деликатный рефакторинг — `SettingsState` уже используется в `SettingsOverlay`.

```kotlin
// Было: SettingsState — просто data class, хранится где-то наверху
// Стало: SettingsViewModel управляет состоянием, SettingsOverlay читает через collectAsState()

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            settingsRepository.get()
                .onSuccess { settings ->
                    _uiState.update { it.copy(settings = settings) }
                }
        }
    }

    fun save(updated: Settings) {
        viewModelScope.launch {
            settingsRepository.update(updated)
                .onSuccess { _uiState.update { it.copy(settings = updated) } }
                .onFailure { e -> _uiState.update { it.copy(error = ErrorMapper.map(e)) } }
        }
    }

    fun backup() {
        viewModelScope.launch {
            settingsRepository.backup()
        }
    }
}
```

```kotlin
// SettingsOverlay — подключение
@Composable
fun SettingsOverlay(onDismiss: () -> Unit) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Рендер на основе uiState.settings
}
```

### 4.4 Список всех ViewModel

| ViewModel | Зависимости |
|-----------|------------|
| `LoginViewModel` | `AuthRepository` |
| `RegisterViewModel` | `AuthRepository` |
| `PasswordRecoveryViewModel` | `AuthRepository` |
| `EmployeeViewModel` | `EmployeeRepository`, `WebcamService` |
| `FaceRecognitionViewModel` | `FaceRecognitionRepository`, `WebcamService` |
| `SettingsViewModel` | `SettingsRepository` |

---

## Итерация 5 — PhotoCaptureDialog + мультифото (3–4 часа)

### 5.1 PhotoCaptureDialog — нужен рефакторинг?

PhotoCaptureDialog — дочерний компонент EmployeesScreen.
Сейчас скорее всего: держит логику камеры сам + вызывает WebcamService напрямую.

**Рекомендация:** вынести логику камеры в `EmployeeViewModel`,
PhotoCaptureDialog оставить чистым Composable без бизнес-логики.

```kotlin
// PhotoCaptureDialog — только UI:
@Composable
fun PhotoCaptureDialog(
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    capturedPhotos: List<ByteArray>,
    previewFrame: ByteArray?,           // текущий кадр с камеры
    canCapture: Boolean,
    canUpload: Boolean,
    onCapture: () -> Unit,
    onUpload: () -> Unit,
    onDismiss: () -> Unit
) { ... }

// EmployeeViewModel управляет камерой:
fun startCamera() { webcamService.start() }
fun stopCamera()  { webcamService.stop() }
fun capturePhoto() {
    val frame = webcamService.currentFrame() ?: return
    // добавить в список, обновить шаг
}
```

### 5.2 UiState регистрации
```kotlin
data class PhotoRegistrationUiState(
    val capturedPhotos: List<ByteArray> = emptyList(),
    val currentStep: Int = 0,
    val totalSteps: Int = 5,
    val instruction: String = "Смотрите прямо в камеру",
    val previewFrame: ByteArray? = null,
    val isUploading: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null,
    val isComplete: Boolean = false
) {
    val canCapture: Boolean get() = capturedPhotos.size < totalSteps && !isUploading
    val canUpload:  Boolean get() = capturedPhotos.size >= 3 && !isUploading
}

private val STEP_INSTRUCTIONS = listOf(
    "Смотрите прямо в камеру",
    "Слегка поверните голову влево",
    "Слегка поверните голову вправо",
    "Слегка наклоните голову вниз",
    "Слегка наклоните голову вверх"
)
```

### 5.3 UI пошаговой регистрации
```
┌────────────────────────────────────────┐
│  Регистрация лица  •  Шаг 2 из 5      │
│  ●●○○○                                 │
├────────────────────────────────────────┤
│                                        │
│        [ Превью камеры ]               │
│                                        │
│  💡 Слегка поверните голову влево      │
│                                        │
├────────────────────────────────────────┤
│  [🖼][🖼][  ][  ][  ]                 │
│                                        │
│  [ Пропустить ]         [ 📸 Снять ]  │
└────────────────────────────────────────┘
```

### 5.4 Отправка нескольких фото
```kotlin
// data/repository/EmployeeRepositoryImpl.kt
override suspend fun registerWithPhotos(
    data: EmployeeCreate,
    photos: List<ByteArray>
): Result<Employee> = runCatching {
    val parts = buildList {
        add(formPart("first_name", data.firstName))
        add(formPart("last_name", data.lastName))
        add(formPart("department", data.department))
        photos.forEachIndexed { i, bytes ->
            add(filePart("photo_$i", bytes, "photo_$i.jpg", "image/jpeg"))
        }
    }
    apiService.registerEmployee(parts).toDomain()
}
```

---

## Итерация 6 — Тесты (2 часа)

### 6.1 Шаблон

```kotlin
class LoginViewModelTest : FunSpec({

    val authRepository = mockk<AuthRepository>()
    lateinit var viewModel: LoginViewModel

    beforeEach { viewModel = LoginViewModel(authRepository) }

    test("успешный логин → isSuccess = true") {
        coEvery { authRepository.login(any(), any()) } returns Result.success("token")

        viewModel.login("admin", "password")

        viewModel.uiState.value.isSuccess shouldBe true
        viewModel.uiState.value.error shouldBe null
    }

    test("неверный пароль → показывает ошибку, не крашится") {
        coEvery { authRepository.login(any(), any()) } returns
            Result.failure(ApiException(401, "Unauthorized"))

        viewModel.login("admin", "wrong")

        viewModel.uiState.value.isSuccess shouldBe false
        viewModel.uiState.value.error shouldNotBe null
    }
})
```

### 6.2 Приоритет покрытия

| Класс | Приоритет | Почему |
|-------|-----------|--------|
| `LoginViewModel` | 🔴 | Критичный путь входа |
| `EmployeeViewModel.registerWithPhotos` | 🔴 | Новая логика мультифото |
| `PasswordRecoveryViewModel` | 🟡 | Новый экран |
| `SettingsViewModel` | 🟡 | Миграция из SettingsState |
| `FaceRecognitionViewModel` | 🟡 | Сложная логика bbox |
| `ErrorMapper` | 🟢 | Чистая функция, быстро |

---

## Итерация 7 — Финальная проверка (1 час)

### Чеклист

```
Код:
[ ] api/ApiService.kt удалён (путь jvmMain)
[ ] AdminStubScreen удалён или реализован
[ ] ServiceLocator.kt удалён
[ ] Voyager удалён из зависимостей и build.gradle.kts
[ ] FormValidator — остался как есть, не тронут
[ ] ./gradlew allTests — все тесты зелёные

Функциональность:
[ ] Login / Register / PasswordRecovery работают
[ ] Employee list, search, delete работает
[ ] Регистрация: 3–5 фото, пошаговый UI
[ ] Face recognition показывает bbox + similarity
[ ] Settings сохраняются между запусками
[ ] Sidebar навигация переключает разделы

Сборка:
[ ] ./gradlew run — без ошибок
[ ] ./gradlew packageDistributable — пакет собирается
```

---

## Итого

| Итерация | Задача | Время |
|----------|--------|-------|
| 0 | Подготовка, удаление мусора (jvmMain) | 30 мин |
| 1 | Зависимости — Voyager НЕ удалять | 1 ч |
| 2 | Voyager → Compose Navigation + вложенный NavGraph | 3–4 ч |
| 3 | ServiceLocator → Koin + WebcamService как singleton | 2–3 ч |
| 4 | ViewModel + ErrorMapper + PasswordRecovery + SettingsState | 4–5 ч |
| 5 | PhotoCaptureDialog рефакторинг + мультифото | 3–4 ч |
| 6 | Kotest тесты | 2 ч |
| 7 | Финальная проверка | 1 ч |
| **Итого** | | **~2.5 дня** |

---

## Что не трогать

| Компонент | Причина |
|-----------|---------|
| `FormValidator` | Чистая утилита, не зависит от архитектуры |
| `ErrorMapper` | Оставить как есть, только перенести вызов в ViewModel |
| `LocalSettingsStorage` | Работает, обернуть в Koin `single {}` |
| Маперы `toDto()` / `toDomain()` | Правильно реализованы, не трогать |
| `ApiClient` / `ApiService` | Перенести в Koin, логику не менять |
