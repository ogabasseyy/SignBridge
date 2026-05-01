# Gate 2 Emergency Grid Review

Date: 2026-05-01

## Result

Passed locally.

The hard-floor demo now works independently of camera, classifier, and Gemma runtime availability.

## Review

- Home screen exposes three large actions: Sign to Speech, Listen, Emergency.
- Emergency screen exposes six large phrase buttons:
  - Help me
  - I am Deaf
  - Please call emergency services
  - I am injured
  - Please write it down
  - I cannot hear you
- Emergency phrase selection is backed by `EmergencyPhrasePresenter` and injected `Speaker`.
- TTS is abstracted behind `Speaker`; Android implementation is `TtsSpeaker`.
- No raw audio, raw video, capture storage, backend service, analytics, crash reporting, or account flow exists.
- Permissions remain limited to camera and microphone.

## TDD Evidence

RED:

- `EmergencyPhrasePresenterTest` failed before `EmergencyPhrasePresenter` and `Speaker` existed.
- `EmergencyScreenTest` failed before `EmergencyScreen` existed.

GREEN:

- Presenter test passed.
- Compose UI test passed on the emulator.

## Emulator Smoke

Evidence file: `docs/verification/test-android-apps-smoke-emergency.md`

Result: passed.

## Verification Commands

- `./gradlew testDebugUnitTest --tests com.signbridge.emergency.EmergencyPhrasePresenterTest`
- `./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.signbridge.ui.EmergencyScreenTest`
- `./gradlew :app:installDebug --console=plain --quiet`
- `adb shell am start --user 0 -n com.signbridge/.MainActivity`
- `adb exec-out uiautomator dump /dev/tty`
- `adb shell input tap 540 1390`
- `adb shell input tap 540 514`
