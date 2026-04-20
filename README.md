# KotlinApp

Desktop client for the **Miit_FaceDetect** face recognition system. Built with **Compose Multiplatform** (JVM target) and **Material3**, providing a modern UI for admin authentication, employee management, face recognition, and application settings.

## Tech Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Language | Kotlin | 2.3.20 |
| UI Framework | Compose Multiplatform | 1.10.3 |
| UI Toolkit | Material3 | 1.10.0-alpha05 |
| Navigation | Voyager | 1.1.0-beta01 |
| HTTP Client | Ktor Client (CIO engine) | 3.0.3 |
| Serialization | kotlinx-serialization-json | 1.7.3 |
| Coroutines | kotlinx-coroutines-swing | 1.10.2 |
| Lifecycle | androidx.lifecycle (viewmodel, runtime) | 2.10.0 |
| Logging | SLF4J | 2.0.16 |
| Testing | JUnit | 4.13.2 |
| Hot Reload | Compose Hot Reload | 1.0.0 |
| Build | Gradle (Kotlin Multiplatform) | — |
| Desktop Packaging | Compose Desktop (Dmg, Msi, Deb) | — |

## Architecture

Clean Architecture with three layers and strict dependency rules:

```
┌─────────────────────────────────────────────────┐
│                 presentation                      │
│   UI composables, Screens (Voyager),            │
│   SettingsState, Theme                           │
│   depends on → domain                            │
├─────────────────────────────────────────────────┤
│                   domain                          │
│   Business models, repository interfaces         │
│   no external dependencies                       │
├─────────────────────────────────────────────────┤
│                    data                           │
│   DTOs, mappers, ApiClient, ApiService,         │
│   LocalSettingsStorage, repository impls         │
│   depends on → domain                            │
└─────────────────────────────────────────────────┘
```

**Dependency rule:** `presentation → domain ← data`. The domain layer knows nothing about data or presentation.

**Cross-cutting components:**
- **ServiceLocator** — singleton DI container, creates and provides ApiClient, ApiService, all repositories
- **util/ErrorMapper** — translates ApiException/NetworkException into user-friendly messages

## Project Structure

```
com.example.kotlinapp
├── main.kt                    # Entry point (Window)
├── App.kt                     # Root composable
├── ServiceLocator.kt          # Singleton DI container
├── domain/
│   ├── model/                  # Business data classes
│   │   ├── Admin.kt
│   │   ├── AuthResult.kt
│   │   ├── Employee.kt
│   │   ├── FaceRecognitionResult.kt
│   │   ├── InviteCode.kt
│   │   └── Settings.kt
│   └── repository/             # Repository interfaces
│       ├── AuthRepository.kt
│       ├── EmployeeRepository.kt
│       ├── FaceRecognitionRepository.kt
│       ├── InviteCodeRepository.kt
│       └── SettingsRepository.kt
├── data/
│   ├── local/                  # Local settings storage
│   │   └── LocalSettingsStorage.kt
│   ├── dto/                    # Serializable DTOs for API
│   │   ├── AuthDto.kt
│   │   ├── EmployeeDto.kt
│   │   ├── FaceRecognitionDto.kt
│   │   ├── InviteCodeDto.kt
│   │   └── SettingsDto.kt
│   ├── mapper/                 # DTO ↔ Domain extension functions
│   │   ├── AuthMappers.kt
│   │   ├── EmployeeMappers.kt
│   │   ├── FaceRecognitionMappers.kt
│   │   ├── InviteCodeMappers.kt
│   │   └── SettingsMappers.kt
│   ├── remote/                 # Network layer
│   │   ├── ApiClient.kt
│   │   ├── ApiService.kt
│   │   └── ApiException.kt
│   └── repository/             # Repository implementations
│       ├── AuthRepositoryImpl.kt
│       ├── EmployeeRepositoryImpl.kt
│       ├── FaceRecognitionRepositoryImpl.kt
│       ├── InviteCodeRepositoryImpl.kt
│       └── SettingsRepositoryImpl.kt
├── api/
│   └── ApiService.kt           # Demo client (dummyjson.com)
├── screen/                     # Voyager Screens
│   ├── HomeScreen.kt
│   ├── LoginAdminScreen.kt
│   ├── RegisterAdminScreen.kt
│   └── AdminStubScreen.kt
├── presentation/
│   └── SettingsState.kt
├── ui/
│   ├── buttons/
│   │   └── LoginButton.kt
│   ├── textfields/
│   │   ├── LoginTextField.kt
│   │   └── PasswordTextField.kt
│   ├── icons/
│   │   ├── SettingsIcon.kt
│   │   └── VisibilityIcon.kt
│   ├── settings/
│   │   └── SettingsOverlay.kt
│   └── theme/
│       ├── Color.kt
│       └── Theme.kt
└── util/
    └── ErrorMapper.kt
```

## Features

- **Admin Authentication** — login, registration with invite codes, JWT Bearer token management
- **Employee Management** — CRUD operations, search, photo capture via webcam, employee statistics
- **Face Recognition** — image upload or webcam capture, real-time bounding box overlay with color-coded confidence levels, match results with employee data enrichment
- **Settings** — theme switching (light/dark), configurable API base URL, face recognition parameters (match threshold, camera resolution, FPS), persistent storage via `java.util.prefs.Preferences`
- **Navigation** — Voyager-based screen navigation with composable section switching (Dashboard, Employees, Face Recognition)

## Conventions

### Naming

| Category | Convention | Example |
|----------|-----------|---------|
| Domain models | `camelCase`, plain data classes | `Employee`, `AuthResult` |
| DTO fields | `snake_case` (matches server JSON) | `employee_id`, `created_at` |
| Domain fields | `camelCase` | `employeeId`, `createdAt` |
| DTO classes | `Dto` suffix | `EmployeeResponseDto` |
| Repository interfaces | `Repository` suffix | `AuthRepository` |
| Repository implementations | `RepositoryImpl` suffix | `AuthRepositoryImpl` |
| Mappers | Extension functions `toDto()` / `toDomain()` | `EmployeeResponseDto.toDomain()` |
| Navigation screens | `Screen` suffix | `HomeScreen`, `LoginAdminScreen` |
| UI states | `State` suffix | `SettingsState` |
| Overlays | `Overlay` suffix | `SettingsOverlay` |

### Serialization

- JSON via `kotlinx-serialization`
- Configuration: `ignoreUnknownKeys = true`, `encodeDefaults = false`, `explicitNulls = false`, `isLenient = true`
- Content negotiation through Ktor `ContentNegotiation` plugin

### Network

- Base URL: `http://localhost:8000` (configurable at runtime)
- Engine: Ktor CIO
- Authentication: JWT Bearer token in `Authorization` header
- Multipart uploads for employee photos and face recognition images

## Build & Run

```bash
# Run desktop application
./gradlew run

# Create distributable package
./gradlew packageDistributable

# Build MSI installer (Windows)
./gradlew packageMsi

# Run tests
./gradlew allTests
```

**Entry point:** `com.example.kotlinapp.MainKt`
**Main function:** `main()` in `main.kt` → `Window` → `App()` composable

## API Reference

**Base URL:** `http://localhost:8000`
**Authentication:** JWT Bearer token in `Authorization` header

### Authentication

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| POST | `/api/v1/admins/login` | No | `{username, password}` | `{access_token, token_type}` |
| POST | `/api/v1/admins/register` | No | `{username, email, password, invite_code}` | `{id, username, email, created_at}` |
| GET | `/api/v1/admins/me` | Yes | — | `{id, username, email, created_at}` |

### Invite Codes

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| POST | `/api/v1/admin/invites` | Yes | `{expires_hours}` | `{id, code, created_by, expires_at, is_used, created_at}` |
| GET | `/api/v1/admin/invites` | Yes | — | `[{InviteCodeResponse}]` |
| DELETE | `/api/v1/admin/invites/{id}` | Yes | — | 204 No Content |

### Employees

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| POST | `/api/v1/employees/register` | Yes | Multipart: form fields + image file | `{EmployeeResponse}` |
| GET | `/api/v1/employees?skip=0&limit=100` | Yes | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/search?q=` | Yes | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/stats` | Yes | — | `{total, active, inactive}` |
| PUT | `/api/v1/employees/{id}` | Yes | `{EmployeeUpdate}` | `{EmployeeResponse}` |
| DELETE | `/api/v1/employees/{id}` | Yes | — | 204 No Content |

### Face Recognition

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| POST | `/api/v1/faces/recognize` | Yes | Multipart: image file | `{faces_detected, results[{bbox, matches[{id, username, similarity}]}]}` |

### Settings

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| GET | `/api/v1/settings` | Yes | — | `{SettingsResponse}` |
| PUT | `/api/v1/settings` | Yes | `{SettingsUpdate}` | `{SettingsResponse}` |
| POST | `/api/v1/settings/backup` | Yes | — | 200 OK |

### Health Check

| Method | Endpoint | Auth | Response |
|--------|----------|------|----------|
| GET | `/health` | No | `{status: "ok"}` |

## Implementation Status

- **domain/** — Models ✅, Repository interfaces ✅
- **data/dto/** — All DTOs ✅
- **data/mapper/** — All mappers ✅
- **data/local/** — LocalSettingsStorage ✅
- **data/remote/** — ApiClient ✅, ApiService ✅, ApiException ✅
- **data/repository/** — All 5 implementations ✅
- **api/** — Demo client (dummyjson.com) ✅
- **screen/** — 4 Voyager Screens ✅
- **presentation/** — SettingsState ✅
- **ui/** — Buttons, text fields, icons, settings, theme ✅
- **util/** — ErrorMapper ✅
