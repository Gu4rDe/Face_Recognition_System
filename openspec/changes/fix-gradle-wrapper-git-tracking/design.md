## Context

The KotlinApp project uses Gradle Wrapper for building. The `.gitignore` file contains a `*.jar` pattern that excludes all JAR files from version control, including `gradle/wrapper/gradle-wrapper.jar`. This JAR is essential for the Gradle Wrapper to function — without it, `./gradlew` fails immediately after cloning with `Unable to access jarfile`.

This is a well-known issue: the Gradle Wrapper JAR should always be committed to version control so that anyone can clone and build the project without installing Gradle manually.

## Goals / Non-Goals

**Goals:**
- Ensure `gradle-wrapper.jar` is tracked by Git so `./gradlew run` works after a fresh `git clone`
- Maintain the `*.jar` exclusion rule to keep other JARs out of the repository

**Non-Goals:**
- Changes to build configuration, dependencies, or application code
- Adding other files to version control (e.g., local build artifacts)
- Modifying the Gradle Wrapper version or configuration

## Decisions

**1. Use Git negation pattern in `.gitignore`**

Add `!gradle/wrapper/gradle-wrapper.jar` after the `*.jar` line in `.gitignore`. This is the standard approach recommended by Gradle and GitHub.

Alternative considered: Removing `*.jar` entirely — rejected because it would allow other unwanted JARs (e.g., build output) to be accidentally committed.

**2. Force-add the file with `git add -f`**

Since the file is currently ignored, `git add -f` is required to override the `.gitignore` rule and stage it. After the negation pattern is added, future changes to the file will be tracked normally.

## Risks / Trade-offs

- **Risk**: The `gradle-wrapper.jar` (~44KB) will increase repository size by a small amount. → Acceptable; this is standard practice and the file is small.
- **Risk**: If Gradle Wrapper is upgraded, the JAR must be re-committed. → Mitigated by the fact that `gradlew wrapper` updates both the properties and the JAR, and the negation rule ensures it stays tracked.