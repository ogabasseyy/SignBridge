# LiteRT-LM Spike

Date: 2026-05-01

## Status

Not enabled yet.

The emulator does not have AICore, so the emulator Prompt API result is not enough to activate the LiteRT-LM fallback. The fallback becomes mandatory only if the physical S24 Ultra cannot run Gemma through ML Kit Prompt API.

## Prepared Path

- Version catalog entry exists:
  - `com.google.ai.edge.litertlm:litertlm-android:0.11.0-rc1`
- App dependency remains commented in `app/build.gradle.kts` until the physical Gate 0 result requires it.
- LiteRT-LM Android docs confirm the Gradle package and `Engine`/`EngineConfig` entry point.

## Activation Checklist

If Prompt API cannot access Gemma on the S24 Ultra:

- Uncomment `implementation(libs.litertlm.android)` in `app/build.gradle.kts`.
- Run `./gradlew :app:dependencies --configuration debugRuntimeClasspath` and record the resolved LiteRT-LM version here.
- Run `./gradlew :app:assembleDebug`.
- Add `LiteRtGemmaClient` and runtime-selection tests before product code depends on Gemma.
- Validate Gemma E2B first, then E4B if E2B works.
- Record model size, backend, total latency for both Gate 0 prompts, peak memory if available, and airplane-mode behavior.
