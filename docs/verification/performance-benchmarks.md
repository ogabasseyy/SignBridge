# Performance Benchmarks

Date: 2026-05-01

## Build Artifacts

| Artifact | Size | Status |
|---|---:|---|
| Debug APK | 85 MB | Built locally |
| `app/src/main/assets/signbridge_phrases_v1.tflite` | 108 B | Placeholder |
| `ml/models/signbridge_phrases_v1.tflite` | 108 B | Placeholder |

## Measured Locally

| Area | Result |
|---|---|
| Android unit tests + connected emulator test + debug build | Passed in 7-8 seconds on retry |
| Python ML fixture tests | Passed 12 tests in about 1.6 seconds |
| Test Android Apps forward smoke | Passed on emulator |
| Test Android Apps Listen typed fallback smoke | Passed on emulator |
| Test Android Apps onboarding/settings smoke | Passed on emulator |

## Not Yet Measured

| Metric | Reason |
|---|---|
| S24 Ultra camera analyzer FPS | Physical device pending |
| MediaPipe Holistic per-frame latency | Real MediaPipe `.task` model not wired yet |
| Classifier inference latency | Current TFLite file is a placeholder |
| Gemma first-token/total latency | Physical S24 AICore/Gemma runtime pending |
| TTS time-to-speech | Physical device pending |
| RAM peak with Gemma E4B | Physical device pending |

## Target Budgets

- Camera analyzer: 15 FPS.
- Landmark extraction: under 50 ms/frame on S24 Ultra.
- Classifier inference: under 100 ms after push-to-sign release.
- Gemma reconstruction: target under 2 seconds for demo phrase.
- Reply condensation: target under 2 seconds for a short transcript.
