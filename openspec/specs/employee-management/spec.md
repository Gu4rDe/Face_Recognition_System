## ADDED Requirements

### Requirement: Отображение списка сотрудников

EmployeeContent SHALL отображать список сотрудников с их основными данными: ФИО (username), отдел (department), должность (position), статус (active/inactive). Данные получаются через ServiceLocator.employeeRepository.getEmployees().

#### Scenario: Успешная загрузка списка

- **WHEN** пользователь открывает раздел Сотрудники
- **THEN** отображается список сотрудников с колонками: ФИО, отдел, должность, статус

#### Scenario: Пустой список

- **WHEN** список сотрудников пуст
- **THEN** отображается сообщение «Сотрудники не найдены»

#### Scenario: Ошибка загрузки

- **WHEN** загрузка списка завершается ошибкой
- **THEN** отображается сообщение об ошибке на русском языке (через ErrorMapper)

### Requirement: Поиск сотрудников

EmployeeContent SHALL предоставлять поле поиска для фильтрации сотрудников. Поиск выполняется через ServiceLocator.employeeRepository.searchEmployees(query).

#### Scenario: Ввод поискового запроса

- **WHEN** пользователь вводит текст в поле поиска
- **THEN** список фильтруется в соответствии с результатами поиска по запросу

#### Scenario: Очистка поиска

- **WHEN** пользователь очищает поле поиска
- **THEN** отображается полный список сотрудников

### Requirement: Форма добавления сотрудника

AddEmployeeDialog SHALL содержать поля: username, email, phone, department, position и кнопку «Создать фото».

Поле employee_id SHALL автоматически генерироваться как EMP-{millis} при отправке формы и не SHALL отображаться в UI.

Поля location, hire_date и переключатель isActive SHALL быть удалены из формы. Значения по умолчанию: location=null, hire_date=null, isActive=true, accessEnabled=true.

Кнопка «Создать фото» SHALL открывать PhotoCaptureDialog вместо JFileChooser.

#### Scenario: Успешное добавление сотрудника

- **WHEN** пользователь заполняет форму (username, email обязательны) и подтверждает фото через PhotoCaptureDialog
- **THEN** employee_id автоматически генерируется, createEmployee вызывается с employeeId=EMP-{millis}, location=null, hire_date=null, isActive=true, accessEnabled=true

#### Scenario: Фото не захвачено

- **WHEN** пользователь не прошёл PhotoCaptureDialog (отменил или не захватил фото)
- **THEN** кнопка «Создать» отключена или отображается предупреждение «Создайте фото»

### Requirement: Удаление сотрудника

EmployeeContent SHALL предоставлять возможность удаления сотрудника через кнопку подтверждения. Удаление выполняется через ServiceLocator.employeeRepository.deleteEmployee(id).

#### Scenario: Успешное удаление

- **WHEN** пользователь подтверждает удаление сотрудника
- **THEN** сотрудник удаляется с сервера, список обновляется, отображается уведомление об успехе

#### Scenario: Ошибка удаления

- **WHEN** удаление завершается ошибкой
- **THEN** отображается сообщение об ошибке на русском языке

### Requirement: Индикатор загрузки

При выполнении асинхронных операций EmployeeContent SHALL отображать индикатор загрузки (прогресс-бар или спиннер).

#### Scenario: Загрузка данных

- **WHEN** выполняется запрос на сервер
- **THEN** отображается индикатор загрузки до завершения операции