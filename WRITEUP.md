# SignBridge Technical Write-Up

## Summary

SignBridge is an offline-first Android MVP for a bounded Deaf signer -> hearing non-signer communication flow. The target demo is a Lagos roadside collision scene where the Deaf user signs or selects a fixed phrase, reviews the text before speech, and uses the phone speaker to communicate.

The repo currently contains an emulator-verified implementation scaffold with fallback flows. It is not yet ready to claim full Gemma 4 E4B offline operation until physical S24 Ultra Gate 0 is completed.

## Gemma Usage Plan

The intended production path is:

1. ML Kit Prompt API through AICore on the S24 Ultra.
2. LiteRT-LM Gemma fallback if Prompt API cannot access Gemma in-app.
3. Hard stop for hackathon submission if no local Gemma runtime works in-app.

Current implementation:

- `GemmaClient` interface isolates reconstruction and reply condensation.
- `MlKitGemmaClient` and `LiteRtGemmaClient` boundaries exist.
- `FakeGemmaClient` keeps the emulator UI testable until physical runtime validation is complete.
- Runtime selection tests ensure the app does not silently fall back to local templates as the primary Gemma path.

## Tool Calling Claim

The current build uses app-level tool orchestration, not native Gemma function calling.

Implemented tools:

- `detect_context(glosses, timeOfDay?)`
- `select_tone(context, urgent)`
- `extract_intent(transcript)`

The README/video should use the phrase **app-level tool orchestration around Gemma output** unless native Gemma tool calling is verified on the selected runtime.

## Sign Recognition Pipeline

Planned:

- CameraX front camera.
- MediaPipe Holistic landmarks.
- 30-frame normalized landmark window.
- Compact TFLite classifier over 25 phrases plus `unknown`.

Current:

- CameraX preview and push-to-sign state are implemented.
- Landmark normalization and tensor-shape contracts are tested.
- TFLite export scripts and metadata contracts exist.
- Public synthetic landmark fixture exists for CI/public review.
- The committed `.tflite` is a valid untrained contract model loaded through the Android LiteRT runtime, documented in `docs/verification/classifier-report.md`.

## Reverse Loop

Planned:

- On-device speech recognition or Gemma native audio if verified.
- Gemma text condensation into one readable sentence.

Current:

- `SpeechToTextClient` boundary exists.
- Listen screen includes typed reply fallback.
- Reply condensation prompt/fallback is tested.
- Physical S24 microphone and speech recognition status remain pending.

## Offline And Privacy

The app declares only:

- Camera permission.
- Microphone permission.

It does not declare internet permission. There is no backend, account system, analytics, crash reporting, cloud sync, or raw media persistence in the hackathon build.

## Verification

Passed locally:

- `./gradlew testDebugUnitTest connectedDebugAndroidTest :app:assembleDebug`
- `.venv/bin/pytest ml/tests -q`

Passed via Test Android Apps emulator QA:

- Emergency grid.
- Camera/Sign to Speech shell.
- Placeholder classifier top-3.
- Forward translation preview.
- Listen typed fallback.
- Onboarding and settings.

See `docs/verification/test-android-apps-qa.md`.

## Release Blockers

Before a real Kaggle submission:

1. Complete physical S24 Ultra Gate 0.
2. Verify in-app Gemma rewrite and reply condensation offline.
3. Wire live ML Kit Prompt API or LiteRT-LM generation.
4. Wire MediaPipe Holistic real landmark extraction.
5. Train/export a real TFLite phrase classifier from private captures.
6. Run the Lagos demo path three times on the S24 Ultra in airplane mode.
