## Why

Users who forget their password have no way to recover access to their admin account. Currently, the only option is to register a new account with a valid invite code, which is inconvenient and doesn't preserve existing account data. This change adds a password recovery flow using invite code verification, consistent with the existing registration flow.

## What Changes

- Add "Забыли пароль?" (Forgot password?) link on the login screen
- Create a new PasswordRecoveryScreen with username, invite code, and new password fields
- Add a new backend endpoint `POST /api/v1/admins/reset-password` for password reset via invite code verification
- Add frontend API layer, repository method, domain model, DTOs, and form validation for password recovery
- All UI text in Russian, following existing patterns

## Capabilities

### New Capabilities

- `password-recovery`: Password reset flow using invite code verification. Covers the frontend screen, API endpoint, repository method, and validation logic.

### Modified Capabilities

<!-- No existing capabilities are modified -->

## Impact

- **Frontend**: New screen (`PasswordRecoveryScreen.kt`), new domain model (`AdminResetPassword`), new DTOs, new API service method, new repository method, updated `LoginAdminScreen.kt` with navigation link, updated `FormValidator.kt`, updated `ErrorMapper.kt`
- **Backend**: New FastAPI endpoint `POST /api/v1/admins/reset-password`
- **Navigation**: Voyager navigation from `LoginAdminScreen` to `PasswordRecoveryScreen`
