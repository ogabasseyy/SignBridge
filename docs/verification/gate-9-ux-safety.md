# Gate 9: UX And Safety Review

Date: 2026-05-01

## Scope Completed

- Added privacy-first `AppSettings` defaults.
- Added model preference fallback handling for E4B/E2B.
- Added disclaimer state reducer.
- Added first-run disclaimer UI.
- Added Settings UI with auto-speak, confidence threshold, voice rate, model preference, and data contribution controls.
- Added Settings route from Home.

## Tests Run

- `./gradlew testDebugUnitTest --tests com.signbridge.settings.AppSettingsTest --tests com.signbridge.onboarding.DisclaimerStateTest`
  - Failed first for missing settings/disclaimer classes, as expected for RED.
  - Passed after implementation.
- `./gradlew testDebugUnitTest :app:assembleDebug`
  - Passed.

## Device And Manual Checks

- Test Android Apps emulator smoke for onboarding and settings passed.
- Evidence: `docs/verification/test-android-apps-smoke-settings.md`
- Screenshots:
  - `docs/verification/test-android-apps-smoke-onboarding.png`
  - `docs/verification/test-android-apps-smoke-settings.png`

## Safety Review

- Auto-speak is off by default.
- Data contribution is off by default.
- Confidence threshold defaults to 0.65.
- Safety disclaimer is shown before first use.
- No network permission, analytics, crash reporting, accounts, or backend were added.
- Current Sign to Speech path still does not auto-speak, so low-confidence speech is blocked by default.

## Risks Discovered

- Disclaimer acceptance is currently in memory only and will show again after process restart. This is safe for the hackathon build, but a persisted preference would be needed for production polish.
- Settings values are currently in memory only. They document and expose the intended controls, but full persistence can be deferred until after the critical Gemma/device gates.

## Decision

Continue.

No safety/privacy blocker found. Persisted settings are not required for the hackathon demo, but the limitation must remain documented if not fixed before release.
