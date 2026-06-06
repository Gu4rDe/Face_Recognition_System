## ADDED Requirements

### Requirement: Пошаговая регистрация с 3–5 фотографиями

EmployeeViewModel SHALL поддерживать PhotoRegistrationUiState с пошаговым UI: 5 шагов (3 обязательных, 2 необязательных). Каждый шаг имеет инструкцию на русском языке.

#### Scenario: Шаг 1 — «Смотрите прямо в камеру»

- **WHEN** пользователь начинает регистрацию фото
- **THEN** currentStep = 0, instruction = «Смотрите прямо в камеру»

#### Scenario: Шаг 2 — «Слегка поверните голову влево»

- **WHEN** пользователь захватил фото на шаге 1
- **THEN** currentStep = 1, instruction = «Слегка поверните голову влево»

#### Scenario: Шаг 3 — «Слегка поверните голову вправо»

- **WHEN** пользователь захватил фото на шаге 2
- **THEN** currentStep = 2, instruction = «Слегка поверните голову вправо»

#### Scenario: Шаг 4 — «Слегка наклоните голову вниз» (необязательный)

- **WHEN** пользователь захватил фото на шаге 3
- **THEN** currentStep = 3, instruction = «Слегка наклоните голову вниз», кнопка «Пропустить» доступна

#### Scenario: Шаг 5 — «Слегка наклоните голову вверх» (необязательный)

- **WHEN** пользователь захватил или пропустил шаг 4
- **THEN** currentStep = 4, instruction = «Слегка наклоните голову вверх», кнопка «Пропустить» доступна

### Requirement: canUpload — минимум 3 фото для загрузки

PhotoRegistrationUiState.canUpload SHALL быть true, если capturedPhotos.size >= 3 и isUploading == false.

#### Scenario: Менее 3 фото — загрузка недоступна

- **WHEN** capturedPhotos.size < 3
- **THEN** canUpload = false, кнопка «Загрузить» отключена

#### Scenario: 3 фото — загрузка доступна

- **WHEN** capturedPhotos.size >= 3 и isUploading == false
- **THEN** canUpload = true, кнопка «Загрузить» активна

### Requirement: Пропуск необязательных шагов

onSkipStep() SHALL увеличивать currentStep для шагов 3 и 4 (индексы >= 3). Вызов onSkipStep() для обязательных шагов (0, 1, 2) SHALL игнорироваться.

#### Scenario: Пропуск шага 4

- **WHEN** currentStep = 3 и onSkipStep() вызывается
- **THEN** currentStep увеличивается на 1, capturedPhotos не изменяется

#### Scenario: Попытка пропустить обязательный шаг

- **WHEN** currentStep < 3 и onSkipStep() вызывается
- **THEN** currentStep не изменяется

### Requirement: PhotoCaptureDialog — event-driven composable

PhotoCaptureDialog SHALL быть stateless composable, принимающим параметры: isOpen, currentStep, totalSteps, instruction, capturedPhotos, onCapture, onSkip, onUpload, onDismiss. Диалог не управляет состоянием камеры — вызывает ViewModel методы.

#### Scenario: Захват фото через ViewModel

- **WHEN** пользователь нажимает «Захватить» в PhotoCaptureDialog
- **THEN** вызывается onCapture(), который делегирует ViewModel.onPhotoCapture() → WebcamService.capture()

#### Scenario: Пропуск шага через ViewModel

- **WHEN** пользователь нажимает «Пропустить» (для шагов 3 и 4)
- **THEN** вызывается onSkip(), который делегирует ViewModel.onSkipStep()

#### Scenario: Загрузка фото

- **WHEN** пользователь нажимает «Загрузить» (canUpload == true)
- **THEN** вызывается onUpload(), который делегирует ViewModel.uploadPhotos()

### Requirement: registerWithPhotos в EmployeeRepository

EmployeeRepository SHALL иметь метод registerWithPhotos(data: EmployeeCreate, photos: List<ByteArray>): Result<Employee>, отправляющий multipart-запрос с 3–5 фотографиями.

#### Scenario: Успешная регистрация с 3 фото

- **WHEN** uploadPhotos() вызывается с 3 захваченными фотографиями
- **THEN** registerWithPhotos() отправляет multipart-запрос с полями формы и файлами photo_0, photo_1, photo_2

#### Scenario: Успешная регистрация с 5 фото

- **WHEN** uploadPhotos() вызывается с 5 захваченными фотографиями
- **THEN** registerWithPhotos() отправляет multipart-запрос с файлами photo_0 … photo_4

#### Scenario: Ошибка при загрузке фото

- **WHEN** сервер возвращает ошибку при multipart-запросе
- **THEN** uiState.error = mapException(e), isUploading = false

### Requirement: Reset photo state

resetPhotoState() SHALL сбрасывать PhotoRegistrationUiState к начальным значениям (emptyList, currentStep=0).

#### Scenario: Сброс состояния фото после завершения

- **WHEN** загрузка сотрудников завершена успешно
- **THEN** resetPhotoState() вызывается, capturedPhotos = emptyList(), currentStep = 0