## ADDED Requirements

### Requirement: Koin модули для DI

Приложение SHALL использовать Koin 4.0.0 для внедрения зависимостей с четырьмя модулями: networkModule, infrastructureModule, repositoryModule, viewModelModule.

#### Scenario: Инициализация Koin

- **WHEN** приложение запускается (main.kt)
- **THEN** startKoin инициализирует все модули: networkModule, infrastructureModule, repositoryModule, viewModelModule

#### Scenario: networkModule предоставляет ApiClient, ApiService, LocalSettingsStorage

- **WHEN** запрашивается ApiClient, ApiService или LocalSettingsStorage
- **THEN** Koin возвращает singleton-экземпляры

#### Scenario: infrastructureModule предоставляет WebcamService

- **WHEN** запрашивается WebcamService
- **THEN** Koin возвращает singleton-экземпляр

#### Scenario: repositoryModule предоставляет все репозитории

- **WHEN** запрашивается AuthRepository, EmployeeRepository, FaceRecognitionRepository, InviteCodeRepository или SettingsRepository
- **THEN** Koin возвращает singleton-экземпляры соответствующих реализаций

#### Scenario: viewModelModule предоставляет все ViewModel

- **WHEN** запрашивается LoginViewModel, RegisterViewModel, PasswordRecoveryViewModel, DashboardViewModel, EmployeeViewModel, FaceRecognitionViewModel или SettingsViewModel
- **THEN** Koin создаёт ViewModel через viewModel { } и инжектирует зависимости

### Requirement: ApiClient — мутабельный singleton

ApiClient SHALL быть зарегистрирован как single в Koin. Поля baseUrl и token SHALL быть мутабельными. Setter baseUrl SHALL вызывать rebuildClient() для пересоздания HTTP-клиента.

#### Scenario: Изменение baseUrl через настройки

- **WHEN** пользователь меняет API URL в SettingsOverlay
- **THEN** SettingsViewModel вызывает apiClient.baseUrl = newUrl, HTTP-клиент пересоздаётся

#### Scenario: Изменение token при логине

- **WHEN** пользователь успешно входит
- **THEN** AuthRepository устанавливает apiClient.token = accessToken

### Requirement: Web与管理Service — class вместо object

WebcamService SHALL быть class (не object) с методами open(), capture(), close(), isAvailable(). WebcamService инжектируется через Koin как single.

#### Scenario: Инжекция WebcamService в ViewModel

- **WHEN** EmployeeViewModel или FaceRecognitionViewModel запрашивает WebcamService
- **THEN** Koin инжектирует singleton-экземпляр WebcamService

### Requirement: Удаление ServiceLocator

ServiceLocator.kt SHALL быть удалён. Все ссылки на ServiceLocator.xxxRepository SHALL быть заменены на инжекцию через Koin ViewModel.

#### Scenario: Код не ссылается на ServiceLocator

- **WHEN** проект компилируется после миграции
- **THEN** отсутствуют импорты и использования ServiceLocator

### Requirement: Экраны получают зависимости через ViewModel

Экраны SHALL получать данные через koinViewModel(), а не через ServiceLocator. ViewModel инжектирует репозитории и сервисы через конструктор.

#### Scenario: DashboardContent получает статистику через ViewModel

- **WHEN** DashboardContent рендерится
- **THEN** данные статистики получаются из DashboardViewModel.uiState.collectAsState()

#### Scenario: EmployeeContent получает список через ViewModel

- **WHEN** EmployeeContent рендерится
- **THEN** данные сотрудников получаются из EmployeeViewModel.uiState.collectAsState()