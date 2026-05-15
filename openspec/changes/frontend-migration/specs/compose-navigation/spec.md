## ADDED Requirements

### Requirement: Навигация через Compose Navigation

Приложение SHALL использовать Compose Navigation (org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10) в качестве навигационного фреймворка. Навигация реализуется через NavHost, NavHostController и sealed class AppScreen.

#### Scenario: Определение маршрутов

- **WHEN** приложение запускается
- **THEN** NavHost создаётся с startDestination = AppScreen.Login.route

#### Scenario: Навигация Login → Main

- **WHEN** пользователь успешно входит
- **THEN**(navController).navigate(AppScreen.Main.withSection("dashboard")) { popUpTo(AppScreen.Login.route) { inclusive = true } } очищает backstack

#### Scenario: Навигация Login → Register

- **WHEN** пользователь нажимает «Зарегистрироваться»
- **THEN** navController.navigate(AppScreen.Register.route) открывает экран регистрации

#### Scenario: Навигация Login → PasswordRecovery

- **WHEN** пользователь нажимает «Восстановить пароль»
- **THEN** navController.navigate(AppScreen.PasswordRecovery.route) открывает экран восстановления

#### Scenario: Навигация PasswordRecovery → Login (назад)

- **WHEN** пользователь нажимает «Назад» на экране восстановления
- **THEN** navController.popBackStack() возвращает на Login

#### Scenario: Logout — очистка backstack

- **WHEN** пользователь нажимает «Выйти»
- **THEN** navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } } очищает весь backstack

### Requirement: AppScreen — sealed class маршрутов

AppScreen SHALL быть sealed class с route: String. Маршруты: Login("login"), Register("register"), PasswordRecovery("password_recovery"), Main("main/{section}") с аргументом section.

#### Scenario: Маршрут Main с section

- **WHEN** вызывается AppScreen.Main.withSection("dashboard")
- **THEN** формируется маршрут "main/dashboard"

#### Scenario: Аргумент section имеет значение по умолчанию

- **WHEN** NavHost обрабатывает маршрут Main без явного section
- **THEN** defaultValue = "dashboard" используется

### Requirement: AppNavGraph — корневой NavHost

AppNavGraph SHALL быть @Composable функцией, принимающей NavHostController, и содержать NavHost с четырьмя composable-маршрутами: Login, Register, PasswordRecovery, Main.

#### Scenario: Рендеринг экранов

- **WHEN** NavHost обрабатывает маршрут
- **THEN** соответствующий @Composable экран рендерится с navController в качестве параметра

### Requirement: Удаление Voyager

Все импорты cafe.adriel.voyager SHALL быть удалены. Экраны больше не реализуют интерфейс Screen. Класс Navigator не используется.

#### Scenario: Компиляция без Voyager

- **WHEN** проект собираруется после миграции
- **THEN** отсутствуют ошибки компиляции, связанные с Voyager

### Requirement: Удаление HomeScreen

HomeScreen SHALL быть удалён. Стартовый маршрут — LoginScreen.

#### Scenario: Приложение запускается на Login

- **WHEN** приложение запускается
- **THEN** отображается экран Login (не HomeScreen)

### Requirement: Экраны принимают NavHostController

Каждый экран SHALL быть @Composable функцией с параметром navController: NavHostController. Экраны: LoginScreen(navController), RegisterScreen(navController), PasswordRecoveryScreen(navController), MainScreen(navController, initialSection).

#### Scenario: LoginScreen получает navController

- **WHEN** LoginScreen рендерится
- **THEN** навигационные переходы выполняются через navController.navigate()

### Requirement: Иконка настроек доступна после SettingsViewModel

До создания SettingsViewModel (Итерация 3) иконка настроек в MainScreen MAY быть временно недоступна. После — SettingsOverlay интегрируется в App() через koinViewModel().

#### Scenario: Настройки работают после миграции

- **WHEN** SettingsViewModel создан и интегрирован в App()
- **THEN** иконка настроек в MainScreen открывает SettingsOverlay с данными из SettingsViewModel