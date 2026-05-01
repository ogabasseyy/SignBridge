# Test Android Apps Smoke: Onboarding And Settings

Date: 2026-05-01

## Scope

Light emulator QA for the first-run disclaimer and Settings route.

## Commands

- `./gradlew testDebugUnitTest :app:assembleDebug`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -n com.signbridge/.MainActivity`
- UI tree inspection with `test-android-apps:android-emulator-qa` helper scripts:
  - `ui_tree_summarize.py`
  - `ui_pick.py`

## Result

Passed.

The emulator displayed the first-run disclaimer before the Home screen. After tapping `I understand`, the Home screen exposed Settings, and Settings rendered:

- Auto-speak off
- Confidence threshold at 65%
- Voice rate at 100%
- Gemma 4 E4B selected
- Data contribution off by default
- Safety disclaimer copy

## Evidence

- Onboarding screenshot: `docs/verification/test-android-apps-smoke-onboarding.png`
- Settings screenshot: `docs/verification/test-android-apps-smoke-settings.png`
- Logcat: `docs/verification/test-android-apps-smoke-settings-logcat.txt`

## Notes

`./gradlew :app:installDebug` hit a Gradle install-task device communication failure once, but direct `adb install -r app/build/outputs/apk/debug/app-debug.apk` succeeded immediately. The APK itself built and installed cleanly.
