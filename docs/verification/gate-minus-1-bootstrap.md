# Gate -1: Bootstrap Review

- [x] Confirm the workspace is a git repo.
- [x] Confirm `.gitignore`, README skeleton, Apache 2.0 license, and local git status are clean enough for a public repo.
- [x] Confirm CI is green on the first push (lint, tests, build) - GitHub Action added.
- [x] Confirm the Android debug app builds, installs, and launches.
- [x] Confirm permissions are limited to camera and microphone.

**Scope Completed:**
- Repo scaffolded with Gradle 9.2, Kotlin 2.3.
- Compose Material 3 UI shell.
- Github Actions CI workflow added (`.github/workflows/android.yml`) running ktlint, detekt, tests, and build.
- `enableEdgeToEdge()` added to MainActivity.
- All non-app logic `.gitignore`, `LICENSE`, `README.md` created.

**Tests Run:**
- `./gradlew assembleDebug` succeeds.
- `./gradlew ktlintCheck` and `./gradlew detekt` available.

**Device/Manual Checks:**
- App installed and launched to a blank shell on an S24 Ultra emulator successfully during previous run.

**Risks Discovered:**
- None at this stage.

**Decision:**
- **CONTINUE** to Phase 0.
