# Test Android Apps Smoke: Emergency Flow

Date: 2026-05-01

Tooling: `test-android-apps:android-emulator-qa` workflow with adb-driven install, launch, UI tree inspection, screenshots, and logcat capture.

## Target

- Emulator: `Medium_Phone_API_36.1`
- Serial: `emulator-5554`
- Package: `com.signbridge`

## Flow

1. Installed debug build with `./gradlew :app:installDebug --console=plain --quiet`.
2. Launched `com.signbridge/.MainActivity`.
3. Confirmed Home screen shows:
   - Offline mode active
   - SignBridge
   - Sign to Speech
   - Listen
   - Emergency
4. Used UI-tree-derived coordinates to tap `Emergency`.
5. Confirmed Emergency screen shows all six hard-floor phrases.
6. Tapped `Help me`.
7. Captured post-tap screenshot and logcat.

## Evidence

- Emergency screenshot: `docs/verification/test-android-apps-smoke-emergency.png`
- Post-tap screenshot: `docs/verification/test-android-apps-smoke-emergency-after-tap.png`
- Launch/navigation logcat: `docs/verification/test-android-apps-smoke-emergency-logcat.txt`
- Post-tap logcat: `docs/verification/test-android-apps-smoke-emergency-after-tap-logcat.txt`

## Result

Passed.

No app crash appeared in the post-tap logcat. The emulator cannot verify audible speaker output, but the presenter unit test and Compose UI test verify that tapping `Help me` calls the injected speaker with `Help me`.
