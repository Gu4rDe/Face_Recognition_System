## Why

The KotlinApp desktop client needs a server communication layer to interact with the Miit_FaceDetect FastAPI backend. Currently there is no HTTP client, authentication handling, or data mapping infrastructure — only placeholder UI files. Without this layer, the app cannot log in, manage employees, perform face recognition, or configure settings.

## What Changes

- Add Ktor HTTP client with JWT Bearer auth interceptor for all API requests
- Add kotlinx-serialization for JSON request/response handling
- Create clean architecture layers: domain models, domain repository interfaces, data DTOs, data mappers, data remote (ApiClient/ApiService), and data repository implementations
- Support all backend endpoints: auth (login/register/me), invite codes (CRUD), employees (CRUD + search + stats), face recognition (recognize), settings (get/update/backup), and health check
- Handle multipart form data for employee registration and face recognition (image uploads)

## Capabilities

### New Capabilities
- `api-client`: Ktor HttpClient configuration with auth token management, content negotiation (JSON), and base URL setup
- `auth-api`: Login, register, and current-user endpoints with JWT token persistence
- `invite-code-api`: Create, list, and delete invite codes
- `employee-api`: Create (with photo upload), list, search, stats, update, and delete employees
- `face-recognition-api`: Submit face images and receive recognition results with bounding boxes and similarity matches
- `settings-api`: Get and update app settings, trigger database backups
- `health-api`: Server health check endpoint

### Modified Capabilities
(None — this is a new layer)

## Impact

- **Dependencies**: Added Ktor client (core, cio, content-negotiation, logging) and kotlinx-serialization-json to `build.gradle.kts` and `libs.versions.toml`
- **New packages**: `domain.model`, `domain.repository`, `data.dto`, `data.mapper`, `data.remote`, `data.repository` under `com.example.kotlinapp`
- **Build**: Requires `kotlinxSerialization` plugin in build config
- **No UI changes**: This change is server-communication only; UI integration is a separate future change