# Day 0 Device Spike

Date: 2026-05-01

## Implemented Spike

- Added `GemmaRuntimeSpike` with ML Kit Prompt API checks for:
  - `ModelReleaseStage.PREVIEW` + `ModelPreference.FULL`
  - `ModelReleaseStage.PREVIEW` + `ModelPreference.FAST`
  - stable default
- Added ML Kit Speech Recognition checks for:
  - Basic
  - Advanced
- Added bounded timeouts so unavailable runtimes do not hang the Gate 0 screen.
- Added `GemmaAvailabilityTest` for the pure runtime-gate decision model.

## Local Emulator Result

Device target:

- Model: `sdk_gphone64_arm64`
- Android release: `16`
- AICore package: not present in `pm list packages`

Observed spike UI:

- Prompt API Preview FULL: `unavailable`
- Prompt API Preview FAST: `unavailable`
- Prompt API Stable: `unavailable`
- Speech Recognition Basic: `error`
- Speech Recognition Advanced: `unavailable`

Screenshot:

![Runtime spike emulator result](/Users/mac/Downloads/Signbridge/docs/verification/runtime-spike-emulator.png)

## Interpretation

This emulator result does not decide the hackathon runtime path. ML Kit GenAI Prompt API relies on Android AICore, and the emulator does not provide the target S24 Ultra AICore/Gemma environment.

The physical S24 Ultra Gate 0 check is still required before treating Prompt API as available for the submission. If Prompt API is unavailable on the S24 Ultra, the next branch is the LiteRT-LM Gemma path, not local templates.

## Commands Run

- `./gradlew testDebugUnitTest --tests com.signbridge.ml.GemmaAvailabilityTest`
- `./gradlew :app:assembleDebug`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- `adb shell am start --user 0 -n com.signbridge/.MainActivity`
- Runtime spike button tapped in the emulator UI.

## Sources Checked

- [ML Kit Prompt API get started](https://developers.google.com/ml-kit/genai/prompt/android/get-started)
- [ML Kit Speech Recognition API](https://developers.google.com/ml-kit/genai/speech-recognition/android)
- [LiteRT-LM Android get started](https://ai.google.dev/edge/litert-lm/android)
