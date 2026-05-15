## ADDED Requirements

### Requirement: Kotest как тестовый фреймворк

Проект SHALL использовать Kotest 5.9.1 (FunSpec) и MockK 1.13.12 для модульного тестирования. Тесты располагаются в source set jvmTest.

#### Scenario: Конфигурация build.gradle.kts

- **WHEN** проект собирается
- **THEN** composeApp/build.gradle.kts содержит testImplementation(libs.kotest.runner), testImplementation(libs.kotest.assertions), testImplementation(libs.mockk) и tasks.withType<Test> { useJUnitPlatform() }

### Requirement: LoginViewModelTest

LoginViewModel SHALL быть покрыт тестами: успешный вход (success), неверные учётные данные (401), сетевая ошибка.

#### Scenario: Успешный вход

- **WHEN** authRepository.login() возвращает AuthResult
- **THEN** uiState.isSuccess = true, uiState.error = null

#### Scenario: Ошибка 401

- **WHEN** authRepository.login() выбрасывает ApiException с кодом 401
- **THEN** uiState.error = mapException(e), uiState.isSuccess = false

#### Scenario: Сетевая ошибка

- **WHEN** authRepository.login() выбрасывает NetworkException
- **THEN** uiState.error = mapException(e), uiState.isSuccess = false

### Requirement: RegisterViewModelTest

RegisterViewModel SHALL быть покрыт тестами: успешная регистрация, неверный invite-код, дублирование.

#### Scenario: Успешная регистрация

- **WHEN** authRepository.register() завершается успешно
- **THEN** uiState.isSuccess = true

#### Scenario: Неверный invite-код

- **WHEN** authRepository.register() выбрасывает ApiException
- **THEN** uiState.error = mapException(e)

### Requirement: PasswordRecoveryViewModelTest

PasswordRecoveryViewModel SHALL быть покрыт тестами: успех, неверный invite-код.

#### Scenario: Успешное восстановление

- **WHEN** authRepository.resetPassword() завершается успешно
- **THEN** uiState.isSuccess = true

#### Scenario: Неверный invite-код

- **WHEN** authRepository.resetPassword() выбрасывает ApiException
- **THEN** uiState.error = mapException(e)

### Requirement: DashboardViewModelTest

DashboardViewModel SHALL быть покрыт тестами: loadStats, checkServer, ошибки.

#### Scenario: Загрузка статистики

- **WHEN** employeeRepository.getEmployeeStats() возвращает EmployeeStats
- **THEN** uiState.stats = EmployeeStats, uiState.statsError = null

#### Scenario: Проверка сервера — подключено

- **WHEN** apiService.healthCheck() возвращает true
- **THEN** uiState.serverStatus = «Подключено»

#### Scenario: Проверка сервера — ошибка

- **WHEN** apiService.healthCheck() выбрасывает исключение
- **THEN** uiState.serverStatus = «Не подключено»

### Requirement: EmployeeViewModelTest

EmployeeViewModel SHALL быть покрыт тестами: loadAll, search, delete, onCapture(), onSkipStep().

#### Scenario: canUpload = false при < 3 фото

- **WHEN** photoState.capturedPhotos.size < 3
- **THEN** photoState.canUpload = false

#### Scenario: canUpload = true при >= 3 фото

- **WHEN** photoState.capturedPhotos.size >= 3
- **THEN** photoState.canUpload = true

#### Scenario: Пропуск шага 4

- **WHEN** currentStep = 3 и onSkipStep() вызывается
- **THEN** currentStep увеличивается, capturedPhotos не изменяется

### Requirement: FaceRecognitionViewModelTest

FaceRecognitionViewModel SHALL быть покрыт тестами: recognize(), порог similarity.

#### Scenario: Успешное распознавание

- **WHEN** faceRecognitionRepository.recognizeFace(bytes) возвращает результат
- **THEN** uiState.result = результат, uiState.error = null

#### Scenario: Ошибка распознавания

- **WHEN** faceRecognitionRepository.recognizeFace(bytes) выбрасывает исключение
- **THEN** uiState.error = mapException(e)

### Requirement: SettingsViewModelTest

SettingsViewModel SHALL быть покрыт тестами: load(), save(), toggleTheme().

#### Scenario: Переключение темы

- **WHEN** toggleTheme() вызывается при isDarkTheme = false
- **THEN** isDarkTheme = true, LocalSettingsStorage обновляется

#### Scenario: Изменение API URL

- **WHEN** applySettings() вызывается с новым apiUrl
- **THEN** apiClient.baseUrl = newUrl, LocalSettingsStorage обновляется

### Requirement: ErrorMapperTest

mapException() SHALL быть покрыт тестами на все типы ApiException и NetworkException.

#### Scenario: ApiException с кодом 401

- **WHEN** mapException(ApiException(401, "...")) вызывается
- **THEN** возвращается «Неверное имя пользователя или пароль»

#### Scenario: NetworkException

- **WHEN** mapException(NetworkException()) вызывается
- **THEN** возвращается «Сервер недоступен»