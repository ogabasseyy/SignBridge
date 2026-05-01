# APK Signing

Date: 2026-05-01

## Current Artifact

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Debug APK size: 104 MB
- Status: built and emulator-tested.

## Release Signing Status

Release signing is not completed in this workspace because the physical S24 Ultra and final runtime/model assets are still pending.

When ready:

1. Generate a release keystore outside the git repo.
2. Store the keystore path and passwords outside git.
3. Add local signing config through ignored Gradle properties or Android Studio release workflow.
4. Build the signed release APK.
5. Install from scratch on the S24 Ultra.
6. Run the exact airplane-mode demo path three times.

Do not commit the keystore, passwords, or signed release secret material.
