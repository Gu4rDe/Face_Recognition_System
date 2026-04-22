## MODIFIED Requirements

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

## ADDED Requirements

_(нет новых требований вне MODIFIED)_