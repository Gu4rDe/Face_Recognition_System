## ADDED Requirements

### Requirement: Dashboard отображает статистику сотрудников

DashboardContent SHALL отображать количество сотрудников: всего, активных, неактивных. Данные получаются через ServiceLocator.employeeRepository.getEmployeeStats().

#### Scenario: Успешная загрузка статистики

- **WHEN** пользователь открывает раздел Dashboard
- **THEN** отображаются карточки с числами: всего (total), активных (active), неактивных (inactive)

#### Scenario: Ошибка загрузки статистики

- **WHEN** загрузка статистики завершается ошибкой
- **THEN** отображается сообщение об ошибке на русском языке (через ErrorMapper)

### Requirement: Dashboard отображает статус подключения к серверу

DashboardContent SHALL отображать текущий статус подключения к серверу. Статус проверяется через ServiceLocator.apiService.healthCheck().

#### Scenario: Сервер доступен

- **WHEN** healthCheck возвращает успешный ответ
- **THEN** статус отображается как «Подключено» с зелёной индикацией

#### Scenario: Сервер недоступен

- **WHEN** healthCheck завершается ошибкой
- **THEN** статус отображается как «Не подключено» с красной индикацией

#### Scenario: Проверка в процессе

- **WHEN** healthCheck ещё выполняется
- **THEN** отображается индикатор загрузки

### Requirement: Dashboard — раздел по умолчанию

При входе в MainScreen раздел Dashboard SHALL быть выбран по умолчанию.

#### Scenario: Первый вход в MainScreen

- **WHEN** пользователь входит в MainScreen
- **THEN** пункт Dashboard выделен в sidebar и DashboardContent отображается в области контента