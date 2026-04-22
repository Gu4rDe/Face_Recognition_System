## Why

При успешной регистрации пользователя сервер возвращает HTTP 403 с сообщением "заблокирован", но клиент показывает通用的 "Доступ запрещён". Это происходит из-за того, что в ErrorMapper не парсится detail из JSON-ответа для кода 403.

## What Changes

- Добавить парсинг `detail` поля из JSON-ответа для HTTP 403 в ErrorMapper
- Использовать `parseDetail(e.message)` как fallback вместо жестко заданного "Доступ запрещён"

## Capabilities

### New Capabilities
Отсутствуют.

### Modified Capabilities
- `error-handling`: Добавить парсинг detail для HTTP 403 аналогично существующему парсингу для 422

## Impact

- Файл: `composeApp/src/jvmMain/kotlin/com/example/kotlinapp/util/ErrorMapper.kt`