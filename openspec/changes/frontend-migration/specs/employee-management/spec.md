## MODIFIED Requirements

### Requirement: Отображение списка сотрудников

EmployeeContent SHALL отображать список сотрудников с их основными данными: ФИО (username), отдел (department), должность (position), статус (active/inactive). Данные получаются через EmployeeViewModel (koinViewModel()), а не через ServiceLocator.

#### Scenario: Успешная загрузка списка

- **WHEN** пользователь открывает раздел Сотрудники
- **THEN** EmployeeViewModel.init() вызывает employeeRepository.listEmployees(), uiState.employees отображается

#### Scenario: Пустой список

- **WHEN** список сотрудников пуст
- **THEN** отображается сообщение «Сотрудники не найдены»

#### Scenario: Ошибка загрузки

- **WHEN** загрузка списка завершается ошибкой
- **THEN** uiState.error = mapException(e), отображается сообщение об ошибке на русском

### Requirement: Поиск сотрудников

EmployeeContent SHALL предоставлять поле поиска для фильтрации сотрудников. Поиск выполняется через EmployeeViewModel.searchEmployees(query).

#### Scenario: Ввод поискового запроса

- **WHEN** пользователь вводит текст в поле поиска
- **THEN** EmployeeViewModel.searchEmployees(query) вызывает employeeRepository.searchEmployees(query)

#### Scenario: Очистка поиска

- **WHEN** пользователь очищает поле поиска
- **THEN** EmployeeViewModel.searchEmployees("") вызывает listEmployees(), отображается полный список

### Requirement: Форма добавления сотрудника

AddEmployeeDialog SHALL содержать поля: username, email, phone, department, position и кнопку «Создать фото». Данные отправляются через EmployeeViewModel.

#### Scenario: Успешное добавление сотрудника

- **WHEN** пользователь заполняет форму и подтверждает фото
- **THEN** EmployeeViewModel.uploadPhotos() вызывает employeeRepository.registerWithPhotos()

### Requirement: Удаление сотрудника

EmployeeContent SHALL предоставлять возможность удаления сотрудника через кнопку подтверждения. Удаление выполняется через EmployeeViewModel.deleteEmployee(id).

#### Scenario: Успешное удаление

- **WHEN** пользователь подтверждает удаление сотрудника
- **THEN** EmployeeViewModel.deleteEmployee(id) вызывает employeeRepository.deleteEmployee(id), затем loadEmployees()

#### Scenario: Ошибка удаления

- **WHEN** удаление завершается ошибкой
- **THEN** uiState.error = mapException(e)