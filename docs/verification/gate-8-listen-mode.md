# Gate 8: Bidirectional Flow Review

Date: 2026-05-01

## Scope Completed

- Added Listen state reducer for `Idle -> Recording -> Transcribing -> Result -> Idle`.
- Added reply-condensation prompt contract and deterministic one-sentence fallback.
- Added typed-reply fallback UI with 32sp large-text output.
- Added `SpeechToTextClient` abstraction and placeholder ML Kit client boundary.
- Wired Home -> Listen navigation.

## Tests Run

- `./gradlew testDebugUnitTest --tests com.signbridge.speech.ReplyCondenserTest --tests com.signbridge.speech.ListenStateTest`
  - Passed.
- `./gradlew testDebugUnitTest :app:assembleDebug`
  - Passed.
- `.venv/bin/pytest ml/tests -q`
  - Passed: 12 tests.

## Device And Manual Checks

- Test Android Apps emulator smoke:
  - Installed debug APK.
  - Launched app on `emulator-5554`.
  - Navigated Home -> Listen.
  - Entered typed reply: `Please show me your insurance`.
  - Confirmed large-text result: `Please show me your insurance.`
  - Evidence: `docs/verification/test-android-apps-smoke-listen.md`
  - Screenshot: `docs/verification/test-android-apps-smoke-listen.png`

## Risks Discovered

- Physical S24 Ultra microphone QA is still pending.
- `MlKitSpeechToTextClient` is intentionally only a boundary until Basic/Advanced speech availability is verified on the S24 Ultra.
- Gemma reply condensation is still blocked by Gate 0 physical-runtime validation. The current fallback preserves facts but is not a valid substitute for the hackathon's required Gemma use.

## Decision

Continue, with reverse-loop scope set to typed fallback until physical-device speech and Gemma runtime checks pass.

If speech recognition remains unavailable offline on the S24 Ultra, keep the typed reply fallback in the video and document the reverse voice loop as pending.
