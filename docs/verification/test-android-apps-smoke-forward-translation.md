# Test Android Apps Smoke: Forward Translation

Date: 2026-05-01

## Scope

Light emulator QA for Sign to Speech after wiring top-3 phrase selection to the `GemmaClient` boundary and manual TTS action.

## Commands

- `./gradlew testDebugUnitTest :app:assembleDebug`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 install -r app/build/outputs/apk/debug/app-debug.apk`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -n com.signbridge/.MainActivity`
- UI tree inspection and coordinate picking with `test-android-apps:android-emulator-qa` helper scripts.

## Result

Passed with earlier placeholder classifier/runtime limitations.

The emulator launched SignBridge, accepted the disclaimer, navigated Home -> Sign to Speech, granted camera permission, recorded enough frames, stopped capture, displayed top-3 predictions, accepted `I am Deaf` from the picker, reconstructed the speakable text through the current `GemmaClient` boundary, and exposed the manual `Speak` action.

Observed large-text output:

`I am Deaf.`

## Evidence

- Screenshot: `docs/verification/test-android-apps-smoke-forward-translation.png`
- Logcat: `docs/verification/test-android-apps-smoke-forward-translation-logcat.txt`

## UI Tree Summary

```text
FrameLayout id=android:id/content bounds=[0,0][1080,2400]
  ScrollView flags=scrollable bounds=[0,0][1080,2400]
    TextView text="unknown (100%)" bounds=[63,836][619,922]
    TextView text="Pick the right phrase" bounds=[63,964][607,1033]
    View flags=clickable,focusable bounds=[63,1054][1017,1222]
      TextView text="1. unknown 100%" bounds=[315,1104][766,1173]
    View flags=clickable,focusable bounds=[63,1243][1017,1411]
      TextView text="2. I am Deaf 0%" bounds=[341,1293][740,1362]
    View flags=clickable,focusable bounds=[63,1432][1017,1600]
      TextView text="3. Please calm down 0%" bounds=[229,1482][851,1551]
    View flags=clickable,focusable bounds=[63,1642][1017,1884]
      TextView text="Sign again" bounds=[356,1717][724,1810]
    TextView text="Selected: I am Deaf" bounds=[63,1926][537,1989]
    TextView text="I am Deaf." bounds=[63,2021][427,2116]
    View flags=clickable,focusable bounds=[63,2148][524,2337]
      TextView text="Speak" bounds=[205,2206][382,2279]
    View flags=clickable,focusable bounds=[556,2148][1017,2337]
      TextView text="Clear" bounds=[712,2206][862,2279]
```

## Superseded By

`docs/verification/test-android-apps-smoke-forward-litert.md` supersedes this smoke for the current classifier runtime path. The newer smoke verifies LiteRT/TFLite model loading with the untrained contract model.

## Notes

The classifier is still using `PlaceholderSignInterpreter`, so this smoke proves UI flow and integration boundaries, not real sign recognition accuracy.
