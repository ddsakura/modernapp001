# Agent Notes

## Android Emulator Startup

This project can be built, installed, and launched without Android Studio.
Prefer the Android CLI wrapper for Android operations. The current official
Android CLI docs state that commands should be invoked as `android <command>`,
for example `android emulator start`, `android run`, `android layout`, and
`android screen capture`. Use lower-level SDK tools such as `adb` or the raw
`emulator` binary only when the Android CLI does not expose the needed action or
as a troubleshooting fallback.

Use Gradle to build the debug APK:

```bash
./gradlew assembleDebug
```

The debug APK is expected at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

When starting existing AVDs from the CLI, use the official Android CLI command
first:

```bash
android emulator start Pixel_9
```

From Codex's non-interactive command environment, run the same official command
inside a detached `screen` session so the emulator process is not cleaned up
when the command runner exits:

```bash
screen -dmS android-pixel9 android emulator start Pixel_9
```

If the emulator window opens briefly and then exits, or if the command reports
success but `adb devices -l` does not show a device, the process may have been
cleaned up by the command environment. The same `android emulator start Pixel_9`
command may work normally from the user's interactive terminal.

Only if the detached `screen` approach still fails, fall back to a cold boot
that skips the saved snapshot. Snapshot-related failures were previously
observed with `Pixel_9` and `Pixel_4_API_33`.

Fallback:

```bash
/Users/ericc/Library/Android/sdk/emulator/emulator @Pixel_9 -no-snapshot-load
```

Expected sequence:

1. `adb devices -l` may initially show `emulator-5554 offline`.
2. Wait for boot completion:

```bash
adb wait-for-device
```

3. Confirm it is online:

```bash
adb devices -l
```

The device should appear as `emulator-5554 device`.

Then deploy and launch:

```bash
android run --device=emulator-5554 --apks=app/build/outputs/apk/debug/app-debug.apk
```

Optional foreground UI verification:

```bash
android layout --device=emulator-5554 --pretty
```

For screenshots and visual coordinate resolution, use Android CLI first:

```bash
android screen capture --output=ui.png --annotate
android screen resolve --screenshot=ui.png --string="input tap #5"
```

For low-level input that is not wrapped by Android CLI, use ADB:

```bash
adb shell input tap <x> <y>
adb shell input keyevent KEYCODE_BACK
```

Skipping snapshot load avoids the repeated open-then-exit failure when a saved
snapshot is bad, but it should be treated as a workaround rather than the normal
first command.
