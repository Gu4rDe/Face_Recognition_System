## Why

The `gradle-wrapper.jar` file is required to run `./gradlew` after cloning the repository, but it is excluded from Git by the `*.jar` pattern in `.gitignore`. This causes a `Unable to access jarfile` error when anyone clones the project and tries to build it, making the project unusable out of the box.

## What Changes

- Add an exclusion rule `!gradle/wrapper/gradle-wrapper.jar` to `.gitignore` so the Gradle wrapper JAR is tracked by Git
- Force-add `gradle/wrapper/gradle-wrapper.jar` to version control

## Capabilities

### New Capabilities

None

### Modified Capabilities

None

## Impact

- **`.gitignore`**: One line added (`!gradle/wrapper/gradle-wrapper.jar`)
- **Git tracking**: `gradle/wrapper/gradle-wrapper.jar` becomes a tracked file
- **Reproducibility**: Project builds successfully after `git clone` without manual setup
- **No code changes**: This is a configuration-only fix