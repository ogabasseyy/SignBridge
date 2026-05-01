# Test Android Apps Smoke: Forward LiteRT Runtime

Date: 2026-05-01

## Scope

Emulator QA for Sign to Speech after replacing the classifier UI path with LiteRT/TFLite runtime loading and adding an analyzer recording gate.

## Commands

- `./gradlew testDebugUnitTest :app:assembleDebug`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -n com.signbridge/.MainActivity`
- UI tree inspection and screenshots through the Test Android Apps adb workflow.

## Result

Passed.

The app loaded the TFLite contract model through the LiteRT runtime path, captured frames, stopped capture, remained in result state, displayed top-3 candidates, and showed the speakable preview.

Observed top-3 output from the untrained contract model:

- `1. I am Deaf 3%`
- `2. Please calm down 3%`
- `3. Please write it down 3%`

Observed speakable preview:

`I am Deaf.`

## Evidence

- Result screenshot: `docs/verification/test-android-apps-smoke-forward-litert.png`
- Translation screenshot: `docs/verification/test-android-apps-smoke-forward-litert-translation.png`
- Logcat: `docs/verification/test-android-apps-smoke-forward-litert-logcat.txt`

## Notes

The classifier model is still untrained. This evidence validates runtime loading, tensor flow, result-state stability, and UI integration, not sign recognition accuracy.
