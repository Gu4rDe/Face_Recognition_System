# Face Recognition System

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.10.3-green.svg)](https://github.com/JetBrains/compose-multiplatform)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg)]()

Desktop client for the **Miit_FaceDetect** face recognition system. Built with **Compose Multiplatform** and **Material3**, providing a modern UI for admin authentication, employee management, face recognition, and application settings.

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Configuration](#configuration)
- [API Reference](#api-reference)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Admin Authentication** — login, registration with invite codes, JWT Bearer token management
- **Employee Management** — CRUD operations, search, photo capture via webcam, employee statistics
- **Face Recognition** — image upload or webcam capture, real-time bounding box overlay with color-coded confidence levels, match results with employee data enrichment
- **Settings** — theme switching (light/dark), configurable API base URL, face recognition parameters (match threshold, camera resolution, FPS), persistent storage
- **Navigation** — Voyager-based screen navigation with composable section switching

## Tech Stack

| Layer | Technology | Version |
|-------|------------|---------|
| Language | Kotlin | 2.3.20 |
| UI Framework | Compose Multiplatform | 1.10.3 |
| UI Toolkit | Material3 | 1.10.0-alpha05 |
| Navigation | Voyager | 1.1.0-beta01 |
| HTTP Client | Ktor Client (CIO) | 3.0.3 |
| Serialization | kotlinx-serialization-json | 1.7.3 |
| Coroutines | kotlinx-coroutines-swing | 1.10.2 |
| Lifecycle | androidx.lifecycle | 2.10.0 |
| Logging | SLF4J | 2.0.16 |
| Testing | JUnit | 4.13.2 |
| Webcam | webcam-capture | — |
| Build | Gradle (Kotlin Multiplatform) | — |

## Architecture

Clean Architecture with three layers and strict dependency rules:

```
presentation ──→ domain ←── data
```

| Layer | Responsibility |
|-------|---------------|
| **presentation** | UI composables, Voyager screens, SettingsState, theming |
| **domain** | Business models, repository interfaces (no external dependencies) |
| **data** | DTOs, mappers, ApiClient, ApiService, LocalSettingsStorage, repository implementations |

**Dependency rule:** `presentation → domain ← data`. The domain layer knows nothing about data or presentation.

**Cross-cutting components:**
- **ServiceLocator** — singleton DI container for ApiClient, ApiService, and all repositories
- **util/ErrorMapper** — translates ApiException/NetworkException into user-friendly messages

## Project Structure

```
composeApp/src/jvmMain/kotlin/com/example/kotlinapp
├── main.kt                    # Entry point (Window)
├── App.kt                     # Root composable
├── ServiceLocator.kt          # Singleton DI container
├── domain/
│   ├── model/                  # Business data classes
│   └── repository/             # Repository interfaces
├── data/
│   ├── local/                  # Local settings storage
│   ├── dto/                    # Serializable DTOs for API
│   ├── mapper/                 # DTO ↔ Domain mappers
│   ├── remote/                 # Network layer (ApiClient, ApiService)
│   └── repository/             # Repository implementations
├── screen/                     # Voyager Screens
├── presentation/               # UI state management
├── ui/                         # Reusable UI components
│   ├── buttons/
│   ├── textfields/
│   ├── icons/
│   ├── settings/
│   └── theme/
└── util/                       # Utility classes
```

## Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| JDK | 17+ | Required for JVM target |
| Gradle | Bundled | Wrapper included (`gradlew` / `gradlew.bat`) |
| OS | Windows / macOS / Linux | Desktop targets only |

## Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd my-frontend-project
   ```

2. **Verify JDK installation**
   ```bash
   java -version
   ```
   Ensure JDK 17 or higher is installed and `JAVA_HOME` is set.

3. **Build the project**
   ```bash
   ./gradlew build
   ```
   On Windows, use `gradlew.bat build`.

## Usage

### Run in development mode

```bash
./gradlew run
```

This launches the desktop application with hot reload enabled.

### Build distributable package

```bash
./gradlew packageDistributable
```

Creates a distributable package for the current OS (`.dmg` for macOS, `.msi` for Windows, `.deb` for Linux).

### Build installer (Windows)

```bash
./gradlew packageMsi
```

Generates an MSI installer for Windows.

### Run tests

```bash
./gradlew allTests
```

### Entry point

- **Main class:** `com.example.kotlinapp.MainKt`
- **Flow:** `main()` → `Window` → `App()` composable

## Configuration

### Runtime Settings

The application provides an in-app settings overlay for:

| Setting | Description | Default |
|---------|-------------|---------|
| Theme | Light / Dark mode | System default |
| API Base URL | Backend server URL | `http://localhost:8000` |
| Match Threshold | Minimum similarity for face match | — |
| Camera Resolution | Webcam capture resolution | — |
| FPS | Camera frames per second | — |

Settings are persisted via `java.util.prefs.Preferences`.

### Network

- **Engine:** Ktor CIO
- **Authentication:** JWT Bearer token in `Authorization` header
- **Serialization:** `kotlinx-serialization` with `ignoreUnknownKeys = true`, `isLenient = true`
- **Uploads:** Multipart for employee photos and face recognition images

## API Reference

**Base URL:** `http://localhost:8000`

### Authentication

| Method | Endpoint | Auth | Body | Response |
|--------|----------|------|------|----------|
| POST | `/api/v1/admins/login` | — | `{username, password}` | `{access_token, token_type}` |
| POST | `/api/v1/admins/register` | — | `{username, email, password, invite_code}` | `{id, username, email, created_at}` |
| GET | `/api/v1/admins/me` | Yes | — | `{id, username, email, created_at}` |

### Employees

| Method | Endpoint | Auth | Body | Response |
|--------|----------|------|------|----------|
| POST | `/api/v1/employees/register` | Yes | Multipart (fields + image) | `{EmployeeResponse}` |
| GET | `/api/v1/employees` | Yes | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/search?q=` | Yes | — | `[{EmployeeResponse}]` |
| GET | `/api/v1/employees/stats` | Yes | — | `{total, active, inactive}` |
| PUT | `/api/v1/employees/{id}` | Yes | `{EmployeeUpdate}` | `{EmployeeResponse}` |
| DELETE | `/api/v1/employees/{id}` | Yes | — | 204 |

### Face Recognition

| Method | Endpoint | Auth | Body | Response |
|--------|----------|------|------|----------|
| POST | `/api/v1/faces/recognize` | Yes | Multipart (image) | `{faces_detected, results}` |

### Invite Codes

| Method | Endpoint | Auth | Body | Response |
|--------|----------|------|------|----------|
| POST | `/api/v1/admin/invites` | Yes | `{expires_hours}` | `{InviteCodeResponse}` |
| GET | `/api/v1/admin/invites` | Yes | — | `[{InviteCodeResponse}]` |
| DELETE | `/api/v1/admin/invites/{id}` | Yes | — | 204 |

### Health Check

| Method | Endpoint | Auth | Response |
|--------|----------|------|----------|
| GET | `/health` | — | `{status: "ok"}` |

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is distributed under the MIT License. See [LICENSE](LICENSE) for details.
