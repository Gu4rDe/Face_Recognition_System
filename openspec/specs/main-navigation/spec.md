## ADDED Requirements

### Requirement: Sidebar отображает навигационные пункты

MainScreen SHALL отображать左侧 NavigationRail с пунктами меню: Dashboard, Сотрудники, Распознавание лиц. Каждый пункт SHALL иметь иконку из Material Icons и текстовую подпись.

#### Scenario: Отображение пунктов меню при входе в MainScreen

- **WHEN** пользователь переходит на MainScreen
- **THEN** sidebar отображает три пункта: Dashboard (иконка Dashboard), Сотрудники (иконка People), Распознавание лиц (иконка Face)

#### Scenario: Выбранный пункт подсвечивается

- **WHEN** пользователь выбирает пункт меню
- **THEN** выбранный пункт визуально подсвечивается (Material3 selected state) и соответствующий контент отображается справа

### Requirement: Sidebar содержит кнопку выхода

MainScreen SHALL отображать кнопку Выйти в нижней части NavigationRail.

#### Scenario: Выход из аккаунта

- **WHEN** пользователь нажимает кнопку Выйти
- **THEN** аккаунт выходит из системы — JWT-токен очищается через ServiceLocator.authRepository.setToken(null), и навигация возвращается на HomeScreen

### Requirement: Переключение разделов через composable

MainScreen SHALL переключать отображаемый контент на основе выбранного пункта меню без использования navigator.push(). Выбранный раздел хранится в состоянии MainScreen как MenuItem.

#### Scenario: Переключение между разделами

- **WHEN** пользователь выбирает пункт Сотрудники, будучи на Dashboard
- **THEN** контент области переключается с DashboardContent на EmployeeContent, sidebar остаётся видимым, стек навигации не изменяется

### Requirement: MainScreen заменяет AdminStubScreen

MainScreen SHALL заменять AdminStubScreen в навигационном графе. После успешного логина или регистрации пользователь SHALL направляться на MainScreen.

#### Scenario: Навигация после входа

- **WHEN** пользователь успешно входит через LoginAdminScreen
- **THEN** навигация ведёт на MainScreen (вместо AdminStubScreen)

#### Scenario: Навигация после регистрации

- **WHEN** пользователь успешно регистрируется через RegisterAdminScreen
- **THEN** навигация ведёт на MainScreen (вместо AdminStubScreen)

### Requirement: MenuItem определяется как sealed interface

MenuItem SHALL быть sealed interface с тремя реализациями: Dashboard, Employees, FaceRecognition. Это обеспечивает exhaustive-when при обработке выбора.

#### Scenario: Компилятор проверяет полноту when-выражений

- **WHEN** разработчик добавляет обработку MenuItem в when-выражение
- **THEN** компилятор требует обработки всех вариантов (Dashboard, Employees, FaceRecognition)