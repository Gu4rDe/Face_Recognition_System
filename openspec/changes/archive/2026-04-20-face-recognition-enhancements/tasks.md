## 1. Визуализация bounding box

- [x] 1.1 Создать composable FaceBoundingBoxOverlay в файле `ui/components/FaceBoundingBoxOverlay.kt`, принимающий ImageBitmap, список FaceResult и Modifier. Отрисовывать прямоугольники через Canvas/drawRect с цветовой кодировкой: зелёный (similarity > 0.8), жёлто-оранжевый (0.6–0.8), красный (< 0.6), серый (нет совпадений)
- [x] 1.2 Реализовать масштабирование координат bbox из оригинального размера ImageBitmap в отображаемый размер с учётом ContentScale.Fit (letterboxing)
- [x] 1.3 Добавить текстовые подписи над прямоугольниками: имя лучшего совпадения и процент сходства; для лиц без совпадений подпись не выводится

## 2. Захват фото с веб-камеры

- [x] 2.1 Добавить состояние `showCameraDialog` в FaceRecognitionContent и кнопку «Снять с камеры» рядом с «Выбрать фото»
- [x] 2.2 Интегрировать PhotoCaptureDialog: при подтверждении захвата — установить ByteArray как источник изображения (photoBytes), отобразить предпросмотр и сделать доступной кнопку «Распознать»
- [x] 2.3 Заменить логику отправки файла на использование общего состояния источника (файл или камера), чтобы оба пути вели к одному вызову recognizeFace(imageBytes)
- [x] 2.4 При переключении источника (файл после камеры или наоборот) — сбрасывать результат распознавания и обновлять предпросмотр

## 3. Настройки распознавания лиц

- [x] 3.1 Расширить SettingsState: добавить наблюдаемые поля matchThreshold (MutableState<Float>), cameraResolution (MutableState<String>), cameraFps (MutableState<Int>); реализовать загрузку через SettingsRepository.getSettings() при открытии диалога
- [x] 3.2 Добавить в SettingsOverlay секцию «Распознавание лиц» после существующих настроек (тема, API URL): Slider для matchThreshold (0.0–1.0, шаг 0.01) с текстовым отображением в %, OutlinedTextField для cameraResolution и cameraFps
- [x] 3.3 Реализовать сохранение настроек распознавания при закрытии диалога: включить matchThreshold, cameraResolution, cameraFps в AppSettingsUpdate и вызвать SettingsRepository.updateSettings()

## 4. Проверка и финализация

- [x] 4.1 Проверить сборку проекта (compile) — отсутствия ошибок компиляции
- [ ] 4.2 Визуально проверить: захват с камеры → распознавание → отображение bounding box с подписями
- [ ] 4.3 Визуально проверить: изменение настроек matchThreshold в SettingsOverlay — сохранение и загрузка