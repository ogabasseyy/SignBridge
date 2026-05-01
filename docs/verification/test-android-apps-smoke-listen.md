# Test Android Apps Smoke: Listen Mode

Date: 2026-05-01

## Scope

Light emulator QA for the Listen flow after adding the typed-reply fallback.

## Commands

- `./gradlew :app:installDebug --console=plain --quiet`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 shell am start -n com.signbridge/.MainActivity`
- UI tree inspection with `test-android-apps:android-emulator-qa` helper scripts:
  - `ui_tree_summarize.py`
  - `ui_pick.py`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 exec-out screencap -p > docs/verification/test-android-apps-smoke-listen.png`
- `$HOME/Library/Android/sdk/platform-tools/adb -s emulator-5554 logcat -d > docs/verification/test-android-apps-smoke-listen-logcat.txt`

## Result

Passed.

The emulator launched SignBridge, navigated Home -> Listen, accepted the typed hearing-person reply `Please show me your insurance`, and displayed the condensed large-text result:

`Please show me your insurance.`

## Evidence

- Screenshot: `docs/verification/test-android-apps-smoke-listen.png`
- Logcat: `docs/verification/test-android-apps-smoke-listen-logcat.txt`

## UI Tree Summary

```text
FrameLayout id=android:id/content bounds=[0,0][1080,2400]
  View flags=clickable,focusable bounds=[63,64][215,190]
    TextView text="Back" bounds=[98,100][180,153]
  TextView text="Offline mode active" bounds=[671,100][985,153]
  TextView text="Listen" bounds=[63,236][293,341]
  TextView text="Please show me your insurance." bounds=[63,388][1017,588]
  View flags=clickable,focusable bounds=[63,635][1017,877]
    TextView text="Listen again" bounds=[340,713][741,800]
  EditText text="Please show me your insurance" flags=clickable,long-clickable,focusable bounds=[63,924][1017,1218]
    TextView text="Type reply" bounds=[105,924][256,966]
  View flags=clickable,focusable bounds=[63,1266][1017,1392]
    TextView text="Show reply" bounds=[450,1302][630,1355]
```

## Notes

This is emulator/plugin evidence only. Physical S24 Ultra microphone and Gemma runtime validation remain separate release-gate items.
