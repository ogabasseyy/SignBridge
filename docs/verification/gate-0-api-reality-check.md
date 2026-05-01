# Gate 0 API Reality Check

Date: 2026-05-01

## Current Decision

Gate 0 is not passed yet because no physical S24 Ultra is connected to this workspace.

The Android spike builds and runs, but the emulator cannot prove Gemma availability for the hackathon. The product implementation can continue behind runtime interfaces and fake/test clients, but the final submission path remains blocked until this gate is completed on the S24 Ultra.

## Emulator Evidence

- Build: passed
- Unit runtime-gate tests: passed
- Installed app: passed
- Launched app: passed
- Runtime spike button: passed
- Prompt API Preview FULL: unavailable
- Prompt API Preview FAST: unavailable
- Prompt API Stable: unavailable
- Speech Recognition Basic: error
- Speech Recognition Advanced: unavailable

Evidence screenshot: `docs/verification/runtime-spike-emulator.png`

## Physical S24 Ultra Evidence Still Needed

Record these before calling Gate 0 passed:

- Exact S24 Ultra model
- Android version
- One UI version
- AICore version
- Google Play services version
- Bootloader locked status
- AI Edge Gallery Gemma E2B offline result
- AI Edge Gallery Gemma E4B offline result
- In-app Prompt API Preview FULL status and both prompt outputs
- In-app Prompt API Preview FAST status and both prompt outputs
- In-app Prompt API Stable status and both prompt outputs
- Speech Recognition Basic status
- Speech Recognition Advanced status

## Required Decision After Physical Run

- If Preview FULL works offline: use it as the default Gemma runtime.
- If only Preview FAST works offline: use that runtime for the demo.
- If Prompt API cannot access Gemma in-app: enable LiteRT-LM immediately.
- If no in-app Gemma runtime works: stop SignBridge as a Gemma 4 Good submission and rescope.
- If Advanced speech is unavailable: use Basic/platform on-device speech recognition and Gemma for text condensation.

## TDD Evidence

RED:

- `GemmaAvailabilityTest` initially failed because `RuntimeStatus`, `PromptRuntimeProbe`, `SpeechRuntimeProbe`, and `ApiRealityGate` did not exist.

GREEN:

- Added `GemmaAvailability.kt`.
- `./gradlew testDebugUnitTest --tests com.signbridge.ml.GemmaAvailabilityTest` passed.

## Verification Commands

- `./gradlew testDebugUnitTest --tests com.signbridge.ml.GemmaAvailabilityTest`
- `./gradlew :app:assembleDebug`
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`
- `adb shell am start --user 0 -n com.signbridge/.MainActivity`
