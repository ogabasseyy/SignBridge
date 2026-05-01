# Gate -1: Bootstrap Review

## Scope Completed

- Repository initialized locally.
- Android scaffold created as a single-module Compose project.
- README skeleton, Apache 2.0 license, `.gitignore`, and Android SDK local configuration added.
- Public GitHub repo created: https://github.com/ogabasseyy/SignBridge
- Gradle wrapper generated with Gradle 9.4.1.
- Initial spike screen launches through `com.signbridge/.MainActivity`.

## Tests And Commands

- `git init`: passed.
- `./gradlew :app:assembleDebug`: first run failed during D8 merge due to Java heap exhaustion.
- Added `gradle.properties` with `org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g -Dfile.encoding=UTF-8`.
- `./gradlew :app:assembleDebug`: passed after Gradle JVM memory settings.
- `adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`: passed.
- `adb -s emulator-5554 shell am start --user 0 -n com.signbridge/.MainActivity`: passed.
- `adb -s emulator-5554 exec-out uiautomator dump /dev/tty`: passed and showed `SignBridge runtime spike` plus `Scaffold ready`.

## Device Checks

- Emulator: `Medium_Phone_API_36.1`, serial `emulator-5554`.
- APK installed successfully.
- Spike activity launched successfully.
- UI tree confirms scaffold text is visible.

## Screenshots Or Video

- Screenshot: `docs/verification/bootstrap-spike.png`

## Risks Discovered

- The machine has Android SDK installed at `/Users/mac/Library/Android/sdk`, but no global `gradle` command. A standard Gradle wrapper is being bootstrapped from the official Gradle distribution.
- Gradle 9.5.0 download was slow; existing cached Gradle 9.4.1 was used to generate the wrapper.
- AGP 9 rejects the legacy `org.jetbrains.kotlin.android` plugin because Kotlin support is built into AGP. The scaffold was updated to use only `com.android.application` plus `org.jetbrains.kotlin.plugin.compose`.
- ADB install initially failed because the emulator was still booting. It passed after `sys.boot_completed=1`.

## Decision

- Continue to Phase 0.

## Next Phase Changes

- Use `--user 0` when launching activities on this emulator.
