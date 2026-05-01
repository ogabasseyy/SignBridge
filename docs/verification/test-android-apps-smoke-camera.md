# Test Android Apps Smoke: Sign To Speech Camera Flow

Date: 2026-05-01

Tooling: `test-android-apps:android-emulator-qa` workflow with adb-driven install, permission reset, launch, UI tree inspection, screenshots, and logcat capture.

## Target

- Emulator: `Medium_Phone_API_36.1`
- Serial: `emulator-5554`
- Package: `com.signbridge`

## Flow

1. Installed debug build with `./gradlew :app:installDebug --console=plain --quiet`.
2. Revoked camera permission with `adb shell pm revoke com.signbridge android.permission.CAMERA`.
3. Launched `com.signbridge/.MainActivity`.
4. Navigated Home -> Sign to Speech using UI-tree-derived coordinates.
5. Confirmed the in-app camera permission state:
   - `Camera permission is needed for signing.`
   - `Allow camera`
6. Tapped `Allow camera`.
7. Accepted the Android permission dialog with `While using the app`.
8. Confirmed the camera screen showed:
   - `Ready`
   - `Start signing`
9. Tapped `Start signing`.
10. Confirmed state reached `Recording 71 frames`.
11. Tapped `Stop`.
12. Confirmed state reached `Processing`.

## Evidence

- Permission screen: `docs/verification/test-android-apps-smoke-camera-permission.png`
- Preview ready screen: `docs/verification/test-android-apps-smoke-camera-preview.png`
- Recording screen: `docs/verification/test-android-apps-smoke-camera-recording.png`
- Processing screen: `docs/verification/test-android-apps-smoke-camera-processing.png`
- Camera logcat: `docs/verification/test-android-apps-smoke-camera-logcat.txt`
- Post-record logcat: `docs/verification/test-android-apps-smoke-camera-after-record-logcat.txt`

## Result

Passed for emulator camera flow.

The smoke confirms CameraX preview, permission state, in-memory analyzer callbacks, and sign capture state transitions. It does not prove MediaPipe Holistic quality because the Holistic `.task` model asset is not bundled yet.
