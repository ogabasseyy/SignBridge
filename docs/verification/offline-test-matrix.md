# Offline Test Matrix

Date: 2026-05-01

## Current Build

- APK: `app/build/outputs/apk/debug/app-debug.apk`
- APK size: 104 MB
- Permissions declared:
  - `android.permission.CAMERA`
  - `android.permission.RECORD_AUDIO`
- No `INTERNET` permission is declared.

## Matrix

| Case | Emulator/Test Android Apps result | Physical S24 result | Notes |
|---|---|---|---|
| Fresh launch | Passed | Pending | First-run disclaimer shown before Home. |
| Airplane mode cold launch | Pending emulator toggle | Pending | Must be run after S24 model downloads. |
| Emergency grid | Passed | Pending | Six emergency buttons render and speak through fake/instrumented speaker test; emulator smoke evidence exists. |
| Camera permission missing | Passed | Pending | Sign to Speech shows permission panel. |
| Camera permission granted | Passed on emulator | Pending | Camera preview/state flow renders; MediaPipe extractor still `NoOpLandmarkExtractor`. |
| Sign capture low confidence | Passed as fallback UI | Pending | Placeholder classifier produces top-3 picker; no auto-speak. |
| Forward phrase -> speakable text | Passed with placeholder runtime | Pending | Selected phrase flows through `GemmaClient` boundary to preview and manual Speak action. |
| Listen typed fallback | Passed | Pending | Typed hearing reply condenses to one sentence. |
| Microphone permission missing | Not yet exercised | Pending | Speech client is a boundary; typed fallback is available. |
| AICore unavailable | Passed at policy level | Pending | Runtime selector chooses LiteRT or hard stop; live runtime unavailable on emulator. |
| TTS locale fallback | Passed in unit tests | Pending | `en-NG` preferred, `en-US` fallback. |

## Verification Commands

- `./gradlew testDebugUnitTest connectedDebugAndroidTest :app:assembleDebug`
  - Passed after clearing the transient emulator install state.
- `.venv/bin/pytest ml/tests -q`
  - Passed: 12 tests.
- `rg "uses-permission|INTERNET" app/src/main/AndroidManifest.xml app/src/main/java app/build.gradle.kts`
  - Confirmed camera and microphone only.

## Release Blockers

- Physical S24 Ultra Gate 0 is still required before claiming in-app Gemma 4 works offline.
- The classifier asset is a valid untrained TFLite contract model, not a trained sign-recognition model.
- MediaPipe Holistic extraction is not yet wired to a downloaded `.task` model asset.
