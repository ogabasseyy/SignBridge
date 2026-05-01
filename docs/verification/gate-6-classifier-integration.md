# Gate 6 Sign-To-Prediction Review

Date: 2026-05-01

## Result

Partially passed.

The Android app integrates the classifier asset contract, 30-frame sliding window, label parsing, top-3 result mapping, and Sign to Speech prediction UI. The checked-in `.tflite` is now a valid untrained contract model, but the Android runtime still uses a placeholder interpreter until real TFLite runtime wiring is added.

## Review

- `SlidingWindowBuffer` is tested for:
  - 30-frame readiness
  - reset behavior
  - overflow behavior
  - tensor ordering
- `SignClassifier` maps interpreter scores to sorted top-3 labels.
- Asset tests verify:
  - labels match `PhraseCatalog` plus `unknown`
  - model asset exists
  - metadata input shape is `[1, 30, 1629]`
  - metadata output shape is `[1, 26]`
  - dtype is `float32`
- Sign to Speech screen now shows top-3 predictions after capture.
- The app does not auto-speak classifier output.
- Emulator smoke showed:
  - `unknown (100%)`
  - `1. unknown 100%`
  - `2. I am Deaf 0%`
  - `3. Please calm down 0%`
- No app fatal crash appeared in the classifier smoke log after fixing the Android ICU regex issue.

## Evidence

- Screenshot: `docs/verification/test-android-apps-smoke-classifier-placeholder.png`
- Logcat: `docs/verification/test-android-apps-smoke-classifier-placeholder-logcat.txt`

## Remaining Gate Work

- Replace placeholder interpreter with real LiteRT/TFLite runtime.
- Replace the untrained contract model with a trained classifier after private-data training.
- Use actual normalized landmark windows instead of zero-filled placeholder frames.
- Verify demo-critical phrase predictions on physical S24 Ultra.
- Keep top-3 fallback if top-1 accuracy is imperfect.

## Verification Commands

- `./gradlew testDebugUnitTest :app:assembleDebug`
- `./gradlew :app:installDebug --console=plain --quiet`
- `adb shell am force-stop com.signbridge`
- `adb shell am start --user 0 -n com.signbridge/.MainActivity`
- `adb exec-out uiautomator dump /dev/tty`
- `adb shell input tap 540 750`
- `adb shell input tap 540 1941`
