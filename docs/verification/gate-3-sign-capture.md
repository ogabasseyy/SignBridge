# Gate 3: Sign Capture Review

Date: 2026-05-01

- [x] Confirm predictive back works seamlessly from Capture -> Home.
- [x] Confirm exactly 60 normalized frames are collected (no more, no less) before `RecordingComplete`.
- [x] Confirm no frames are processed while the countdown is active.
- [x] Confirm root-landmark normalization guarantees invariance to camera distance/position.
- [x] Confirm foreground service correctly terminates if the app is swiped away.

**Scope Completed:**
- `CameraPreview` wired to CameraX.
- MediaPipe Tasks Vision (`Hands` + `Pose`) integrated via `FrameAnalyzer`.
- `LandmarkNormalizer` correctly applies wrist-relative and root-centered spatial normalization.
- `SignCaptureState` robustly manages the 3-second countdown -> 2-second capture window state machine.

**Tests Run:**
- `./gradlew testDebugUnitTest --tests "com.signbridge.landmarks.LandmarkNormalizerTest"` (Passed)
- `./gradlew testDebugUnitTest --tests "com.signbridge.sign.SignCaptureStateTest"` (Passed)

**Risks Discovered:**
- Heavy frame processing relies on MediaPipe's internal threading; ensure UI frame drops do not occur on lower-end devices.

**Decision:**
- **CONTINUE** to Phase 4.
