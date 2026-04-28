## ADDED Requirements

### Requirement: Password recovery via invite code
The system SHALL allow an admin user to reset their password by providing their username, a valid invite code, and a new password that meets complexity requirements.

#### Scenario: Successful password reset
- **WHEN** an admin submits a valid username, a valid non-expired invite code, and a new password meeting complexity requirements
- **THEN** the system resets the admin's password and returns a success response

#### Scenario: Invalid username
- **WHEN** an admin submits a username that does not exist
- **THEN** the system returns a 404 error with a message indicating the user was not found

#### Scenario: Invalid or expired invite code
- **WHEN** an admin submits an invite code that is invalid, expired, or already revoked
- **THEN** the system returns a 403 error with a message indicating the invite code is invalid

#### Scenario: Weak new password
- **WHEN** an admin submits a new password that does not meet complexity requirements (min 8 chars, at least one letter, one digit, one special character)
- **THEN** the system returns a 422 error with a message describing the password requirements

#### Scenario: Missing required fields
- **WHEN** an admin submits the form with one or more required fields (username, invite code, new password) left blank
- **THEN** the system returns a 422 error indicating missing required fields

### Requirement: Frontend password recovery screen
The application SHALL provide a PasswordRecoveryScreen accessible from the login screen, allowing the user to enter their username, invite code, new password, and confirm password.

#### Scenario: Navigate to recovery screen
- **WHEN** a user clicks "Забыли пароль?" on the login screen
- **THEN** the PasswordRecoveryScreen is displayed

#### Scenario: Client-side validation errors
- **WHEN** a user submits the recovery form with invalid field values (e.g., blank fields, passwords not matching, weak password)
- **THEN** the form displays inline validation errors and does not send a request to the server

#### Scenario: Successful recovery navigation
- **WHEN** the password reset succeeds
- **THEN** the user is navigated back to the login screen to sign in with the new password

#### Scenario: Server error display
- **WHEN** the password reset request fails with a server error (e.g., 403, 404, 422, 5xx)
- **THEN** a Russian-language error message is displayed explaining the failure

### Requirement: Backend reset-password endpoint
The backend SHALL expose a `POST /api/v1/admins/reset-password` endpoint that accepts a JSON body with `username`, `invite_code`, and `new_password` fields.

#### Scenario: Endpoint receives valid request
- **WHEN** a POST request is sent to `/api/v1/admins/reset-password` with valid `username`, `invite_code`, and `new_password`
- **THEN** the endpoint validates the invite code, resets the user's password, and returns a 200 success response

#### Scenario: Endpoint receives invalid JSON
- **WHEN** a POST request is sent with malformed JSON or missing fields
- **THEN** the endpoint returns a 422 error
