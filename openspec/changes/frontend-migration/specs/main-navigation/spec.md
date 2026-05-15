## MODIFIED Requirements

### Requirement: Sidebar отображает навигационные пункты

MainScreen SHALL отображать NavigationRail с пунктами меню: Dashboard, Сотрудники, Распознавание лиц. Каждый пункт SHALL иметь иконку из Material Icons и текстовую подпись. Выбранный раздел хранится как MainSection enum (не MenuItem sealed interface).

#### Scenario: Отображение пунктов меню при входе в MainScreen

- **WHEN** пользователь переходит на MainScreen
- **THEN** sidebar отображает три пункта: Dashboard (иконка Dashboard), Сотрудники (иконка People), Распознавание лиц (иконка Face)

#### Scenario: Выбранный пункт подсвечивается

- **WHEN** пользователь выбирает пункт меню
- **THEN** выбранный пункт визуально подсвечивается (Material3 selected state) и соответствующий контент отображается справа

### Requirement: Sidebar содержит кнопку выхода

MainScreen SHALL отображать кнопку Выйти в нижней части NavigationRail. При нажатии SHALL вызываться authRepository.setToken(null) через ViewModel, а навигация выполняется через navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } }.

#### Scenario: Выход из аккаунта

- **WHEN** пользователь нажимает кнопку Выйти
- **THEN** authRepository.setToken(null) очищает JWT-токен, navController.navigate(AppScreen.Login.route) { popUpTo(0) { inclusive = true } } очищает весь backstack

### Requirement: Переключение разделов через mutableStateOf

MainScreen SHALL переключать отображаемый контент на основе var currentSection: MainSection через remember { mutableStateOf(...) }. Вложенный NavHost НЕ используется.

#### Scenario: Переключение между разделами

- **WHEN** пользователь выбирает пункт Сотрудники, будучи на Dashboard
- **THEN** currentSection = MainSection.EMPLOYEES, контент переключается на EmployeeContent(), sidebar остаётся видимым

## REMOVED Requirements

### Requirement: MenuItem определяется как sealed interface

**Reason**: Заменён на MainSection enum для совместимости с Compose Navigation. MenuItem.kt удаляется.

**Migration**: Использовать MainSection enum с тремя значениями: DASHBOARD, EMPLOYEES, FACE_RECOGNITION

### Requirement: MainScreen заменяет AdminStubScreen

**Reason**: AdminStubScreen уже не существует в коде. MainScreen — единственный экран после входа.

**Migration**: Навигация после входа ведёт на MainScreen через AppScreen.Main.withSection("dashboard")