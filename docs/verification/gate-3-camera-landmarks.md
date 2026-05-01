# Gate 3 Camera Privacy And Landmark Review

Date: 2026-05-01

## Result

Partially passed.

Camera preview, permission handling, frame analyzer callbacks, capture state, and landmark tensor normalization are implemented and verified. Full MediaPipe Holistic landmark extraction is not complete until the app bundles a Holistic Landmarker `.task` asset and validates overlay points on the physical S24 Ultra.

## Review

- Camera permission flow is understandable and was smoke-tested from a revoked permission state.
- CameraX front-camera preview starts on the emulator after permission grant.
- `FrameAnalyzer` closes every `ImageProxy` in a `finally` block.
- Raw frames are not persisted.
- Repository search found no camera-path file output APIs such as `FileOutputStream`, `MediaStore`, `VideoCapture`, `cacheDir`, or `filesDir` in app source.
- `LandmarkNormalizer` returns the classifier contract shape: `543 * 3 = 1629` float values.
- Missing hands and face are zero-padded.
- Sign capture state transitions were tested:
  - Idle -> Recording
  - Recording -> Processing
  - Processing -> Result
  - Reset -> Idle
- MediaPipe Tasks Vision dependency is present at `0.10.33`.
- `LandmarkExtractor` interface and no-op extractor scaffold are present.

## Remaining Gate Work

- Add `holistic_landmarker.task` to app assets or document an approved model download step.
- Implement a `HolisticLandmarkExtractor` backed by MediaPipe `HolisticLandmarker`.
- Map MediaPipe pose, left-hand, right-hand, and face landmarks into `LandmarkFrame`.
- Verify overlay points on physical S24 Ultra.
- Record S24 preview responsiveness and landmark latency.

## TDD Evidence

RED:

- `LandmarkNormalizerTest` failed before `LandmarkFrame`, `LandmarkPoint`, and `LandmarkNormalizer` existed.
- `SignCaptureStateTest` failed before `SignCaptureState`, `SignCaptureAction`, and `SignCaptureReducer` existed.

GREEN:

- Focused Phase 3 tests passed.
- Full unit test suite passed.
- Connected Compose test suite passed.

## Emulator Smoke

Evidence file: `docs/verification/test-android-apps-smoke-camera.md`

Result: passed for camera permission, preview, recording, and processing states.

## Verification Commands

- `./gradlew testDebugUnitTest --tests com.signbridge.landmarks.LandmarkNormalizerTest --tests com.signbridge.sign.SignCaptureStateTest`
- `./gradlew testDebugUnitTest connectedDebugAndroidTest :app:assembleDebug`
- `./gradlew :app:installDebug --console=plain --quiet`
- `adb shell pm revoke com.signbridge android.permission.CAMERA`
- `adb shell am start --user 0 -n com.signbridge/.MainActivity`
- `adb exec-out uiautomator dump /dev/tty`
- `adb shell input tap 540 750`
- `adb shell input tap 237 619`
- `adb shell input tap 540 1228`
- `adb shell input tap 540 1941`
