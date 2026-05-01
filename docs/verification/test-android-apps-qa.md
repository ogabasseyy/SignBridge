# Test Android Apps QA

Date: 2026-05-01

## Status

Passed for emulator/plugin QA. Physical S24 QA remains separate in `docs/verification/physical-s24-qa.md`.

## Environment

- Emulator serial: `emulator-5554`
- AVD label observed by Gradle: `Medium_Phone_API_36.1(AVD) - 16`
- Package: `com.signbridge`
- APK: `app/build/outputs/apk/debug/app-debug.apk`

## Automated Verification

- `./gradlew testDebugUnitTest connectedDebugAndroidTest :app:assembleDebug`
  - Passed.
  - Note: one earlier connected-test run failed with `INSTALL_FAILED_INSUFFICIENT_STORAGE`; after the stale install state cleared, `connectedDebugAndroidTest` passed and the full command passed.
- `.venv/bin/pytest ml/tests -q`
  - Passed: 12 tests.

## ADB/Test Android Apps Smoke Evidence

| Flow | Result | Evidence |
|---|---|---|
| Emergency grid | Passed | `docs/verification/test-android-apps-smoke-emergency.md` |
| Camera/Sign to Speech shell | Passed | `docs/verification/test-android-apps-smoke-camera.md` |
| Placeholder classifier top-3 | Passed | `docs/verification/test-android-apps-smoke-classifier-placeholder.png` |
| Forward translation preview | Passed | `docs/verification/test-android-apps-smoke-forward-litert.md` |
| Listen typed fallback | Passed | `docs/verification/test-android-apps-smoke-listen.md` |
| Onboarding and settings | Passed | `docs/verification/test-android-apps-smoke-settings.md` |

## Current Limitations

- Emulator QA cannot validate S24 AICore/Gemma availability.
- Emulator QA cannot validate actual S24 camera, speaker, microphone, or offline TTS voice availability.
- Current sign recognition uses LiteRT/TFLite runtime with an untrained contract model; real landmarks and trained weights are still pending.
