## 1. Модели и навигация

- [x] 1.1 Создать sealed interface MenuItem в screen/MenuItem.kt с объектами Dashboard, Employees, FaceRecognition
- [x] 1.2 Создать MainScreen.kt в пакете screen/ — Voyager Screen с NavigationRail sidebar и composable-переключением контента
- [x] 1.3 Реализовать NavigationRail с пунктами Dashboard, Сотрудники, Распознавание лиц и кнопкой Выйти внизу
- [x] 1.4 Заменить навигацию в LoginAdminScreen.kt: navigator.push(AdminStubScreen()) -> navigator.push(MainScreen())
- [x] 1.5 Заменить навигацию в RegisterAdminScreen.kt: navigator.push(AdminStubScreen()) -> navigator.push(MainScreen())
- [x] 1.6 Удалить AdminStubScreen.kt

## 2. Dashboard

- [x] 2.1 Создать screen/DashboardScreen.kt с composable-функцией DashboardContent() — корневой layout с карточками статистики
- [x] 2.2 Реализовать загрузку статистики сотрудников через ServiceLocator.employeeRepository.getEmployeeStats() с отображением total/active/inactive
- [x] 2.3 Реализовать проверку статуса сервера через ServiceLocator.apiService.healthCheck() с индикацией «Подключено»/«Не подключено»
- [x] 2.4 Добавить обработку ошибок через ErrorMapper.mapException() и индикаторы загрузки

## 3. Управление сотрудниками

- [x] 3.1 Создать screen/EmployeeScreen.kt с composable-функцией EmployeeContent()
- [x] 3.2 Реализовать список сотрудников с колонками: ФИО, отдел, должность, статус (через ServiceLocator.employeeRepository.getEmployees())
- [x] 3.3 Реализовать поиск сотрудников через ServiceLocator.employeeRepository.searchEmployees(query)
- [x] 3.4 Создать диалог/форму добавления сотрудника с полями: employee_id, username, email, phone, department, position, location, hire_date, is_active и выбор фото
- [x] 3.5 Реализовать создание сотрудника через ServiceLocator.employeeRepository.createEmployee() с multipart-загрузкой фото
- [x] 3.6 Реализовать удаление сотрудника с диалогом подтверждения через ServiceLocator.employeeRepository.deleteEmployee(id)
- [x] 3.7 Добавить обработку ошибок и индикаторы загрузки для всех операций

## 4. Распознавание лиц

- [x] 4.1 Создать screen/FaceRecognitionScreen.kt с composable-функцией FaceRecognitionContent()
- [x] 4.2 Реализовать выбор файла изображения и предпросмотр перед распознаванием
- [x] 4.3 Реализовать распознавание лиц через ServiceLocator.faceRecognitionRepository.recognizeFace(imageBytes)
- [x] 4.4 Отобразить результаты: faces_detected, список совпадений с username, similarity, bounding box
- [x] 4.5 Добавить обработку ошибок через ErrorMapper.mapException() и индикатор загрузки

## 5. Интеграция и проверка

- [x] 5.1 Проверить полный навигационный поток: HomeScreen -> LoginAdminScreen -> MainScreen -> переключение разделов -> выход
- [x] 5.2 Проверить сборку проекта ./gradlew run без ошибок компиляции