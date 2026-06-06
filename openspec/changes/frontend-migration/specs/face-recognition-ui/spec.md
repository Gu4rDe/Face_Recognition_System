## MODIFIED Requirements

### Requirement: Загрузка фото для распознавания

FaceRecognitionContent SHALL предоставлять два режима загрузки фотографии: выбор файла через JFileChooser и захват с веб-камеры через WebcamService (инжектированный через ViewModel). Оба режима устанавливают источник изображения (ByteArray) для последующей отправки через FaceRecognitionViewModel.recognize(bytes).

#### Scenario: Успешная загрузка файла и распознавание

- **WHEN** пользователь выбирает файл фотографии и нажимает «Распознать»
- **THEN** FaceRecognitionViewModel.recognize(bytes) вызывается, uiState.result обновляется

#### Scenario: Успешный захват с камеры и распознавание

- **WHEN** пользователь нажимает «Снять с камеры», захватывает фото через WebcamService и нажимает «Распознать»
- **THEN** FaceRecognitionViewModel.recognize(bytes) вызывается с захваченным ByteArray

#### Scenario: Ошибка распознавания

- **WHEN** recognize(bytes) завершается ошибкой
- **THEN** uiState.error = mapException(e)

### Requirement: Отображение результатов распознавания

FaceRecognitionContent SHALL отображать результаты распознавания из FaceRecognitionViewModel.uiState. Результаты обогащаются данными сотрудников из employeeMap.

#### Scenario: Лица обнаружены с совпадениями

- **WHEN** uiState.result не null и uiState.employeeMap содержит данные
- **THEN** для каждого совпадения отображается bounding box с подписью «{username} ({similarity%})»

### Requirement: Захват фото с веб-камеры через ViewModel

FaceRecognitionContent SHALL предоставлять кнопку «Снять с камеры» для захвата фото. Захват выполняется через FaceRecognitionViewModel.openWebcam() / captureFromWebcam() / closeWebcam(), которые делегируют инжектированному WebcamService.

#### Scenario: Захват с камеры через ViewModel

- **WHEN** пользователь нажимает «Снять с камеры»
- **THEN** ViewModel.openWebcam() открывает камеру, ViewModel.captureFromWebcam() захватывает фото