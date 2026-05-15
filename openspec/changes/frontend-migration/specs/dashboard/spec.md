## MODIFIED Requirements

### Requirement: Dashboard отображает статистику сотрудников

DashboardContent SHALL отображать количество сотрудников: всего, активных, неактивных. Данные получаются через DashboardViewModel (koinViewModel()), а не через ServiceLocator.

#### Scenario: Успешная загрузка статистики

- **WHEN** пользователь открывает раздел Dashboard
- **THEN** DashboardViewModel.init() вызывает employeeRepository.getEmployeeStats(), uiState.stats отображается

#### Scenario: Ошибка загрузки статистики

- **WHEN** загрузка статистики завершается ошибкой
- **THEN** uiState.statsError = mapException(e), отображается сообщение об ошибке на русском

### Requirement: Dashboard отображает статус подключения к серверу

DashboardContent SHALL отображать текущий статус подключения к серверу. Статус проверяется через DashboardViewModel.checkServer(), который вызывает apiService.healthCheck().

#### Scenario: Сервер доступен

- **WHEN** healthCheck возвращает успешный ответ
- **THEN** uiState.serverStatus = «Подключено»

#### Scenario: Сервер недоступен

- **WHEN** healthCheck завершается ошибкой
- **THEN** uiState.serverStatus = «Не подключено»

#### Scenario: Проверка в процессе

- **WHEN** healthCheck ещё выполняется
- **THEN** uiState.isCheckingServer = true, отображается индикатор загрузки