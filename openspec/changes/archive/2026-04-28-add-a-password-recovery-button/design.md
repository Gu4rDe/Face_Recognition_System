## Context

The app is a Kotlin Multiplatform Compose Desktop application using Voyager for navigation, Material 3 for UI, Ktor Client for networking, and a clean architecture pattern (domain/data/screen layers). The backend is a FastAPI (Python) server. Currently, authentication supports login and registration (via invite codes), but there is no password recovery mechanism. Users who forget their password have no self-service recovery path.

**Constraints:**
- All UI text must be in Russian
- Password recovery uses invite code verification (same mechanism as registration)
- Must follow existing patterns: DTOs in `data/dto/`, mappers in `data/mapper/`, domain models in `domain/model/`, validation in `util/FormValidator`, error mapping in `util/ErrorMapper`
- Voyager navigation pattern: screens are classes implementing `Screen` with `@Composable Content()`
- `ServiceLocator` provides singleton access to repositories

## Goals / Non-Goals

**Goals:**
- Add a "Забыли пароль?" link on the login screen that navigates to a password recovery screen
- Create a PasswordRecoveryScreen with username, invite code, new password, and confirm password fields
- Add backend endpoint `POST /api/v1/admins/reset-password` that validates the invite code and resets the password
- Follow existing code patterns and conventions throughout

**Non-Goals:**
- Email-based password recovery (out of scope)
- Security question-based recovery (out of scope)
- Password recovery for non-admin users (this is admin-only)
- Rate limiting or brute-force protection on the reset endpoint (handled separately)

## Decisions

### 1. Recovery mechanism: Invite code verification
**Decision:** Use invite code verification for password reset, matching the existing registration flow.
**Rationale:** The app already uses invite codes for admin registration. Reusing this mechanism keeps the flow consistent and leverages existing backend invite code validation logic.
**Alternatives considered:**
- Email link recovery: Requires email infrastructure, not currently set up
- Security questions: Adds complexity, no existing data model for this

### 2. Frontend: Separate Voyager screen for recovery
**Decision:** Create `PasswordRecoveryScreen` as a new Voyager `Screen`, pushed from `LoginAdminScreen`.
**Rationale:** Follows the existing pattern where `RegisterAdminScreen` is pushed from `LoginAdminScreen`. On success, pop back to `LoginAdminScreen` so the user can log in with the new password.
**Alternatives considered:**
- Inline recovery in `LoginAdminScreen`: Would make the screen too complex
- Dialog-based recovery: Less consistent with existing patterns

### 3. Backend endpoint design
**Decision:** `POST /api/v1/admins/reset-password` accepting `{username, invite_code, new_password}` — validates invite code, resets password, returns a simple success message (no auto-login token).
**Rationale:** The frontend will handle auto-login after reset by calling the existing login endpoint. The reset endpoint only needs to confirm the reset succeeded. This keeps the endpoint simple and focused.
**Alternatives considered:**
- Return a token directly: Would duplicate login logic in the reset endpoint
- Two-step flow (request + confirm): Overkill for invite-code-based recovery

### 4. Domain model: `AdminResetPassword`
**Decision:** Add a `AdminResetPassword(username, inviteCode, newPassword)` domain model alongside `AdminLogin` and `AdminRegister`.
**Rationale:** Consistent with existing domain model patterns.

### 5. Form validation: Add `validateConfirmPassword`
**Decision:** Add a `validateConfirmPassword(password, confirmPassword)` method to `FormValidator`.
**Rationale:** Reuses the existing validation utility, keeps validation logic centralized.

## Risks / Trade-offs

- **[Risk] Invite code reuse**: If the backend marks invite codes as consumed after registration, a user who registered with a code won't be able to use the same code for password reset. → **Mitigation:** Backend should validate that the invite code is valid (not expired, not revoked) but may allow reuse for password reset, or the admin can generate a new code.
- **[Risk] No email fallback**: If the user loses their invite code, they cannot recover their password. → **Mitigation:** Admins can generate new invite codes from the settings panel (existing functionality).
- **[Trade-off] No rate limiting**: The reset endpoint could be brute-forced. → **Mitigation:** Rate limiting can be added later as a separate concern.
