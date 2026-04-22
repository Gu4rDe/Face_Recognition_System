## 1. Зависимости и сервис камеры

- [x] 1.1 Добавить зависимость com.github.sarxos:webcam-capture в libs.versions.toml и build.gradle.kts
- [x] 1.2 Создать service/WebcamService.kt — singleton с методами open(), capture(): ByteArray?, close(), isAvailable(): Boolean
- [x] 1.3 Реализовать конвертацию BufferedImage -> ByteArray (JPEG) в WebcamService

## 2. PhotoCaptureDialog

- [x] 2.1 Создать ui/dialogs/PhotoCaptureDialog.kt — Composable диалог с предпросмотром камеры, кнопкой Захватить, предпросмотром захваченного фото
- [x] 2.2 Реализовать предпросмотр камеры в реальном времени с использованием LaunchedEffect и WebcamService
- [x] 2.3 Реализовать обработку ошибки «камера не найдена» — показ сообщения вместо предпросмотра
- [x] 2.4 Реализовать кнопку Захватить — заморозка текущего кадра, кнопка Переснять для повторного захвата
- [x] 2.5 Добавить чекбокс «Я принимаю [Политику использования]» с кликабельной ссылкой
- [x] 2.6 Реализовать отключение кнопки Сохранить до установки чекбокса политики
- [x] 2.7 Реализовать открытие текста политики по клику на ссылку (AlertDialog с текстом политики)
- [x] 2.8 Реализовать кнопки Сохранить (возвращает ByteArray) и Отмена (возвращает null)
- [x] 2.9 Гарантировать вызов WebcamService.close() в onDispose при любом способе закрытия диалога

## 3. Обновление формы сотрудника

- [x] 3.1 Убрать поле employeeId из AddEmployeeDialog, генерировать EMP-{System.currentTimeMillis()} при создании
- [x] 3.2 Убрать поля location, hireDate, isActive (переключатель) из AddEmployeeDialog; установить isActive=true, accessEnabled=true, location=null, hireDate=null по умолчанию
- [x] 3.3 Заменить JFileChooser на кнопку «Создать фото», открывающую PhotoCaptureDialog
- [x] 3.4 Обновить валидацию формы: username и email обязательны, выбранное фото обязательно (из PhotoCaptureDialog)
- [x] 3.5 Передавать photoBytes из результата PhotoCaptureDialog вместо File(photoPath).readBytes()

## 4. Интеграция и проверка

- [x] 4.1 Проверить сборку проекта compileKotlinJvm без ошибок
- [x] 4.2 Проверить полный поток: открытие AddEmployeeDialog -> Создать фото -> захват -> принятие политики -> сохранение -> создание сотрудника