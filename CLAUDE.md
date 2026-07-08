# ModernApp001 Claude Guide

## Project Overview

This is an Android/Kotlin Gradle project. Keep changes focused on the requested
task and avoid unrelated refactors or formatting churn.

## Build And Test Commands

Use these commands from the repository root:

```bash
./gradlew test
./gradlew assembleDebug
```

For instrumented Android tests, use:

```bash
./gradlew connectedAndroidTest
```

The debug APK is expected at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Android CLI Notes

This project can be built, installed, and launched without Android Studio.
Prefer the Android CLI wrapper for Android operations:

```bash
android emulator start Pixel_9
android run --device=emulator-5554 --apks=app/build/outputs/apk/debug/app-debug.apk
android layout --device=emulator-5554 --pretty
```

Use lower-level SDK tools such as `adb` only when the Android CLI does not
expose the needed action or as a troubleshooting fallback.

## Contribution Expectations

- Follow the existing Kotlin, Android, Gradle, and resource-file patterns.
- Add or update focused tests when behavior changes.
- Do not commit credentials, tokens, APKs, screenshots, build outputs, or local
  machine-specific files.
- Prefer small pull requests with a clear summary and validation notes.
- When changing UI behavior, mention whether manual or emulator verification was
  performed.

