## ADDED Requirements

### Requirement: LoginViewModel управляет состоянием экрана входа

LoginViewModel SHALL иметь StateFlow<LoginUiState> с полями isLoading, isSuccess, error. Метод login(username, password) SHALL вызывать authRepository.login() и обновлять uiState.

#### Scenario: Успешный вход

- **WHEN** пользователь вводит корректные учётные данные и нажимает «Войти»
- **THEN** LoginViewModel вызывает authRepository.login(), uiState.isSuccess = true, навигация переходит на MainScreen

#### Scenario: Неверные учётные данные (401)

- **WHEN** сервер возвращает 401 Unauthorized
- **THEN** uiState.error = сообщение об ошибке на русском (через mapException)

#### Scenario: Сетевая ошибка при входе

- **WHEN** сервер недоступен
- **THEN** uiState.error = сообщение об ошибке на русском (через mapException)

### Requirement: RegisterViewModel управляет состоянием экрана регистрации

RegisterViewModel SHALL иметь StateFlow<RegisterUiState> с полями isLoading, isSuccess, error. Метод register(username, email, password, inviteCode) SHALL вызывать authRepository.register().

#### Scenario: Успешная регистрация

- **WHEN** пользователь вводит корректные данные и invite-код
- **THEN** RegisterViewModel вызывает authRepository.register(), uiState.isSuccess = true, навигация переходит на MainScreen

#### Scenario: Неверный invite-код

- **WHEN** сервер отклоняет invite-код
- **THEN** uiState.error = сообщение об ошибке

#### Scenario: Дублирование username

- **WHEN** сервер возвращает ошибку дублирования
- **THEN** uiState.error = соответствующее сообщение на русском

### Requirement: PasswordRecoveryViewModel управляет состоянием экрана восстановления

PasswordRecoveryViewModel SHALL иметь StateFlow<PasswordRecoveryUiState> с полями isLoading, isSuccess, error. Метод resetPassword(username, inviteCode, newPassword) SHALL вызывать authRepository.resetPassword().

#### Scenario: Успешное восстановление пароля

- **WHEN** пользователь вводит корректные данные
- **THEN** uiState.isSuccess = true

#### Scenario: Неверный invite-код при восстановлении

- **WHEN** сервер отклоняет invite-код
- **THEN** uiState.error = сообщение об ошибке

### Requirement: DashboardViewModel управляет статистикой и статусом сервера

DashboardViewModel SHALL иметь StateFlow<DashboardUiState> с полями stats, statsError, isLoadingStats, serverStatus, isCheckingServer. Методы loadStats() и checkServer() вызывают соответствующие репозитории.

#### Scenario: Загрузка статистики сотрудников

- **WHEN** DashboardViewModel.init()
- **THEN** loadStats() вызывает employeeRepository.getEmployeeStats(), uiState.stats обновляется

#### Scenario: Ошибка загрузки статистики

- **WHEN** getEmployeeStats() завершается ошибкой
- **THEN** uiState.statsError = mapException(e)

#### Scenario: Проверка статуса сервера

- **WHEN** DashboardViewModel.init()
- **THEN** checkServer() вызывает apiService.healthCheck(), uiState.serverStatus = "Подключено" или "Не подключено"

### Requirement: EmployeeViewModel управляет списком сотрудников и фото-регистрацией

EmployeeViewModel SHALL иметь StateFlow<EmployeeListUiState> и StateFlow<PhotoRegistrationUiState>. Методы: loadEmployees(), searchEmployees(query), deleteEmployee(id), а также onPhotoCapture(), onSkipStep(), resetPhotoState(), uploadPhotos().

#### Scenario: Загрузка списка сотрудников

- **WHEN** EmployeeViewModel.init()
- **THEN** loadEmployees() вызывает employeeRepository.listEmployees()

#### Scenario: Поиск сотрудников

- **WHEN** пользователь вводит текст поиска
- **THEN** searchEmployees(query) вызывает employeeRepository.searchEmployees(query)

#### Scenario: Удаление сотрудника

- **WHEN** пользователь подтверждает удаление
- **THEN** deleteEmployee(id) вызывает employeeRepository.deleteEmployee(id), затем loadEmployees()

### Requirement: FaceRecognitionViewModel управляет распознаванием лиц

FaceRecognitionViewModel SHALL иметь StateFlow<FaceRecognitionUiState> с полями photoBytes, isLoading, result, employeeMap, error. Метод recognize(bytes) загружает результат и обогащает данными сотрудников.

#### Scenario: Успешное распознавание

- **WHEN** recognize(bytes) вызывается
- **THEN** uiState.result содержит FaceRecognitionResult, uiState.employeeMap содержит данные сотрудников

#### Scenario: Ошибка распознавания

- **WHEN** recognize(bytes) завершается ошибкой
- **THEN** uiState.error = mapException(e)

### Requirement: SettingsViewModel управляет настройками приложения

SettingsViewModel SHALL иметь StateFlow<SettingsUiState> с полями isDarkTheme, apiUrl, serverStatus, isCheckingServer, matchThreshold, cameraResolution, cameraFps, isLoadingSettings, loadError, saveError, showSettings. Методы: loadSettings(), applySettings(), toggleTheme(), checkServerConnection().

#### Scenario: Загрузка настроек

- **WHEN** SettingsViewModel.init()
- **THEN** isDarkTheme и apiUrl загружаются из LocalSettingsStorage

#### Scenario: Сохранение настроек

- **WHEN** пользователь нажимает «Применить»
- **THEN** applySettings() сохраняет URL в LocalSettingsStorage, обновляет apiClient.baseUrl, отправляет settings на сервер

#### Scenario: Переключение темы

- **WHEN** пользователь переключает тему
- **THEN** toggleTheme() обновляет isDarkTheme и сохраняет в LocalSettingsStorage

### Requirement: Все ViewModel используют mapException() для обработки ошибок

Все ViewModel SHALL вызывать top-level функцию mapException() из util/ErrorMapper.kt для преобразования исключений в пользовательские сообщения.

#### Scenario: ApiException преобразуется в сообщение

- **WHEN** репозиторий выбрасывает ApiException
- **THEN** ViewModel вызывает mapException(e) и сохраняет результат в uiState.error

### Requirement: Logout через ViewModel

Logout SHALL очищать JWT-токен через authRepository.setToken(null) и навигировать на Login с очисткой backstack.

#### Scenario: Выход из системы

- **WHEN** пользователь нажимает «Выйти»
- **THEN** authRepository.setToken(null) очищает токен, navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }