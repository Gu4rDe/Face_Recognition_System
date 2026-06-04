# AGENTS.md — Face Recognition System (frontend)

Kotlin Compose Desktop (только JVM). Требует JDK 17+.

## Команды
- `gradlew run` — запустить
- `gradlew build` — собрать
- `gradlew allTests` — тесты (JUnit 4)
- `gradlew packageDistributable` — дистрибутив
- `gradlew packageMsi` — MSI для Windows

## Стек
Kotlin 2.3.20 + Compose Multiplatform 1.10.3 + Material3 1.10.0-alpha05 + Voyager 1.1.0-beta01 + Ktor CIO 3.0.3 + kotlinx-serialization + JUnit 4

## Архитектура
Clean Architecture: `presentation → domain ← data`
- `domain/` — модели, интерфейсы репозиториев (только Kotlin stdlib)
- `data/` — DTO, мапперы, ApiClient/ApiService, реализации репозиториев
- UI: `screen/`, `ui/`, `presentation/SettingsState.kt`

Точка входа: `main.kt` (`com.example.kotlinapp.MainKt`) → `Window` → `App()` → `Navigator(HomeScreen())`

## DI
`ServiceLocator` (object) — singleton контейнер. Инициализируется лениво при первом обращении. Содержит `ApiClient`, `ApiService`, все репозитории.

## Навигация
Voyager: `Navigator(HomeScreen())` с `SlideTransition`.  
Экраны реализуют `cafe.adriel.voyager.core.screen.Screen`.  
Screens: `HomeScreen`, `LoginAdminScreen`, `RegisterAdminScreen`, `DashboardScreen`, `EmployeeScreen`, `FaceRecognitionScreen`, `MainScreen`, `PasswordRecoveryScreen`.

## Сеть
- `ApiClient` — Ktor HttpClient (CIO), JWT токен, перестраивает клиент при смене `baseUrl`
- `ApiService` — все API вызовы, `safeCall()` пробрасывает `ApiException` / `NetworkException`
- Default API URL: `http://89.104.74.119:8000` (в коде, не localhost)
- `ErrorMapper.mapException()` — маппит ошибки на русские сообщения
- `api/ApiService.kt` — демо-клиент dummyjson.com (можно удалить)

## Локальное хранение
`LocalSettingsStorage` — `java.util.prefs.Preferences` (тема + API URL)

## Конвенции
- DTO: snake_case, `@Serializable`, суффикс `Dto`
- Domain модели: camelCase
- Мапперы: extension functions `toDto()` / `toDomain()`
- Repository: `interface XRepository` / `class XRepositoryImpl`
- `FormValidator` — русские сообщения об ошибках
- Окно стартует максимизированным (`WindowPlacement.Maximized`)

## Тесты
- JUnit 4 + kotlin-test-junit
- 4 файла в `jvmTest/kotlin/com/example/kotlinapp/`
- Нет интеграционных тестов

## Примечания
- `WebcamService` в `service/`, создаётся вручную (не через DI)
- `app.ico` — только для MSI пакета
- Иконка окна: `loadAppIcon()` из `app.png` (resources)
- OpenSpec workspace в `openspec/` для управления изменениями
