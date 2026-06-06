## MODIFIED Requirements

### Requirement: Настройка порога распознавания лиц

SettingsOverlay SHALL предоставлять управление порогом совпадения (matchThreshold) через Slider в диапазоне от 0.0 до 1.0. Значение SHALL получаться из SettingsViewModel.uiState.collectAsState(), а не из SettingsState.

#### Scenario: Загрузка текущего порога

- **WHEN** диалог настроек открывается
- **THEN** SettingsViewModel.init() загружает settings с сервера, uiState.matchThreshold отображается в слайдере

#### Scenario: Изменение порога

- **WHEN** пользователь перемещает слайдер matchThreshold
- **THEN** uiState.matchThreshold обновляется немедленно через ViewModel

#### Scenario: Сохранение порога на сервер

- **WHEN** пользователь нажимает «Применить»
- **THEN** SettingsViewModel.applySettings() отправляет matchThreshold на сервер через settingsRepository.updateSettings()

### Requirement: Настройка параметров камеры

SettingsOverlay SHALL предоставлять поля для настройки разрешения камеры (cameraResolution) и частоты кадров (cameraFps). Значения SHALL получаться из SettingsViewModel.uiState.collectAsState().

#### Scenario: Загрузка параметров камеры

- **WHEN** диалог настроек открывается
- **THEN** поля cameraResolution и cameraFps отображают текущие значения из SettingsViewModel

#### Scenario: Изменение параметров камеры

- **WHEN** пользователь изменяет параметры
- **THEN** uiState обновляется, сохранение происходит при applySettings()

### Requirement: Настройка API URL и темы

SettingsOverlay SHALL предоставлять поле для API URL и переключатель темы. API URL SHALL обновлять apiClient.baseUrl через SettingsViewModel при applySettings().

#### Scenario: Изменение API URL

- **WHEN** пользователь изменяет API URL и нажимает «Применить»
- **THEN** SettingsViewModel.applySettings() обновляет apiClient.baseUrl, LocalSettingsStorage, и отправляет на сервер

#### Scenario: Переключение темы

- **WHEN** пользователь нажимает переключатель темы
- **THEN** SettingsViewModel.toggleTheme() обновляет uiState.isDarkTheme и LocalSettingsStorage

### Requirement: Проверка подключения к серверу из настроек

SettingsOverlay SHALL предоставлять кнопку «Проверить подключение». Проверка выполняется через SettingsViewModel.checkServerConnection(), вызывающий apiService.healthCheck().

#### Scenario: Успешная проверка

- **WHEN** apiService.healthCheck() возвращает true
- **THEN** uiState.serverStatus = «Подключено»

#### Scenario: Неудачная проверка

- **WHEN** apiService.healthCheck() выбрасывает исключение
- **THEN** uiState.serverStatus = «Не подключено»

## REMOVED Requirements

### Requirement: Группировка настроек распознавания лиц

**Reason**: Настройки распознавания лиц (matchThreshold, cameraResolution, cameraFps) остаются в SettingsOverlay, но доступ к ним теперь через SettingsViewModel, а не через отдельную секцию.

**Migration**: Настройки распознавания лиц остаются сгруппированными в SettingsOverlay, но состояние управляется через SettingsViewModel.uiState