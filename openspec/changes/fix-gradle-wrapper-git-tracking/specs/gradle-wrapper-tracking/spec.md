## ADDED Requirements

### Requirement: gradle-wrapper.jar is tracked by Git

The `gradle/wrapper/gradle-wrapper.jar` file SHALL be tracked by Git so that the project builds successfully after a fresh `git clone`.

#### Scenario: Fresh clone builds successfully

- **WHEN** a developer clones the repository and runs `./gradlew run`
- **THEN** the command executes without the `Unable to access jarfile` error

#### Scenario: gitignore excludes other JARs but not the wrapper

- **WHEN** `.gitignore` is evaluated
- **THEN** the `*.jar` pattern excludes all JAR files except `gradle/wrapper/gradle-wrapper.jar`, which is explicitly allowed via a negation pattern