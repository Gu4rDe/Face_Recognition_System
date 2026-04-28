## 1. Backend — Reset Password Endpoint

- [x] 1.1 Create `POST /api/v1/admins/reset-password` endpoint in FastAPI accepting `{username, invite_code, new_password}`
- [x] 1.2 Implement invite code validation (check existence, not expired, not revoked)
- [x] 1.3 Implement password reset logic (hash new password, update admin record)
- [x] 1.4 Return 200 success response on completion, appropriate error codes (404, 403, 422) on failure

## 2. Frontend — Domain Models and DTOs

- [x] 2.1 Add `AdminResetPassword(username: String, inviteCode: String, newPassword: String)` to `domain/model/Admin.kt`
- [x] 2.2 Add `ResetPasswordRequestDto(username, invite_code, new_password)` and `ResetPasswordResponseDto(message)` to `data/dto/AuthDto.kt`
- [x] 2.3 Add mapper `ResetPasswordRequestDto.toDomain()` or `AdminResetPassword.toDto()` in `data/mapper/AuthMappers.kt`

## 3. Frontend — API and Repository Layer

- [x] 3.1 Add `resetPassword(username, inviteCode, newPassword)` method to `ApiService.kt` calling `POST /api/v1/admins/reset-password`
- [x] 3.2 Add `suspend fun resetPassword(reset: AdminResetPassword): String` to `AuthRepository` interface
- [x] 3.3 Implement `resetPassword()` in `AuthRepositoryImpl` calling the API service

## 4. Frontend — Validation and Error Mapping

- [x] 4.1 Add `validateConfirmPassword(password: String, confirmPassword: String)` to `FormValidator`
- [x] 4.2 Update `ErrorMapper` to handle reset-password-specific error messages (403 for invalid invite code, 404 for user not found)

## 5. Frontend — Password Recovery Screen

- [x] 5.1 Create `PasswordRecoveryScreen.kt` as a Voyager Screen with username, invite code, new password, confirm password fields
- [x] 5.2 Add client-side validation on submit using `FormValidator`
- [x] 5.3 Wire up `ServiceLocator.authRepository.resetPassword()` call with loading state and error handling
- [x] 5.4 On success, navigate back to `LoginAdminScreen` via `navigator.pop()`

## 6. Frontend — Navigation Integration

- [x] 6.1 Add "Забыли пароль?" TextButton to `LoginAdminScreen.kt` that navigates to `PasswordRecoveryScreen`

## 7. Tests

- [x] 7.1 Add unit tests for `FormValidator.validateConfirmPassword()`
- [x] 7.2 Add unit tests for `ErrorMapper` reset-password error scenarios
- [x] 7.3 Add integration test for `AuthRepository.resetPassword()` in `ServerCommunicationTest`
