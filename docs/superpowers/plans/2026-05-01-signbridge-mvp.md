# SignBridge MVP Implementation Plan

> **For inline execution:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Subagents are explicitly disallowed for this project. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a credible offline-first Android MVP that demonstrates Deaf signer -> spoken sentence -> hearing reply text for one Lagos roadside scenario before the May 18, 2026 Kaggle deadline.

**Architecture:** Use a single-module Kotlin/Compose Android app. The forward path is CameraX -> MediaPipe landmarks -> small LiteRT/TFLite phrase classifier -> Gemma 4 via ML Kit Prompt API or LiteRT-LM -> Android TTS. The reverse path is ML Kit Speech Recognition or platform offline speech recognition -> Gemma text condensation -> large-text display, with native Gemma audio treated as a stretch until verified on the target device.

**Tech Stack:** Kotlin 2.x, Jetpack Compose Material 3, CameraX, MediaPipe Tasks, LiteRT/TFLite, ML Kit GenAI Prompt API, LiteRT-LM as Gemma fallback runtime, ML Kit GenAI Speech Recognition where available, Android TextToSpeech, Python/TensorFlow/Keras training scripts, Kaggle Notebook for training.

---

## Plan Review Findings

- The earlier plan was test-covered, not strictly test-driven. It mentioned tests but did not require a red/green/refactor cycle before production code.
- The earlier plan inherited a subagent recommendation from the planning template. That conflicts with the project instruction. Execution is now inline only.
- The earlier checkpoints were useful but too soft. This version adds review gates with explicit evidence, stop/continue decisions, and rollback/cut-line checks.
- The highest-risk assumptions remain AICore/Gemma 4 app access, offline speech recognition on S24 Ultra, classifier accuracy, and physical-device QA. Those are now front-loaded or gated.
- Review upgrade on May 1: fallback must remain Gemma-compliant, classifier export must use an explicit TensorFlow -> LiteRT/TFLite path, repo bootstrap must happen early, app-level tools must not be overclaimed as native Gemma function calling, Listen state tests must be explicit, and Test Android Apps QA must run before final QA.

## Execution Contract

- No subagents. Do not call `spawn_agent`, `send_input`, or `wait_agent` for this project.
- Work inline in this workspace.
- Use TDD for all deterministic app logic, ML data utilities, prompt builders, JSON parsing, settings logic, state reducers, and fallback routing.
- Hardware wrappers may start as spikes, but production-facing behavior must be isolated behind interfaces and covered by tests using fakes.
- Every phase ends with a review gate. At each gate, stop implementation, inspect the diff, run the listed verification, update the gate notes, and decide continue, cut scope, or repair.
- Keep commits phase-sized or smaller once the repo is initialized.
- Do not submit a SignBridge build to the Gemma hackathon unless at least one Gemma runtime path works in the app: ML Kit Prompt API, LiteRT-LM, or another documented local Gemma integration. Local sentence templates are allowed only as in-app resilience when Gemma is temporarily unavailable, not as the hackathon's primary AI implementation.

## TDD Operating Rules

For each testable unit:

1. Write the failing test first.
2. Run only that test and confirm it fails for the expected reason.
3. Write the smallest production code that passes.
4. Run the focused test and confirm it passes.
5. Run the relevant broader test group.
6. Refactor only after green.
7. Record the command output summary in the phase gate notes.

Do not write production logic first for pure Kotlin/Python behavior. Acceptable exceptions are Android/Gradle scaffolding, dependency wiring, visual Compose layout shell, and physical-device spikes. Even then, move behavior into tested units immediately after the spike.

## Review Gate Template

Each gate creates or updates `docs/verification/gate-N-<name>.md` with:

- Scope completed.
- Tests run and result.
- Device/manual checks run and result.
- Screenshots or video clips captured when UI or device behavior changed.
- Risks discovered.
- Decision: continue, repair before continuing, or cut scope.
- Next phase changes, if any.

## Live-Docs Corrections To The PRD

- Keep the product scope: one scenario, 25 phrases, offline-first, no backend, no accounts, no analytics.
- Reopen the AICore assumption: AI Edge Gallery proves the model can run locally, but it does not prove third-party app access. The app must call `checkStatus()` for the exact ML Kit Prompt API model config on the S24 Ultra.
- Treat native Prompt API tool calling as not guaranteed. Android's April 2026 blog says Prompt API support for tool calling, structured output, system prompts, and thinking mode is planned during preview. For the MVP, implement an app-level tool loop that can later swap to native Prompt API tool calls.
- Treat Gemma native audio on the S24 Ultra as not guaranteed. Gemma 4 supports audio in Hugging Face workflows, but ML Kit Speech Recognition's advanced GenAI mode is currently documented for Pixel 10. Ship a reverse loop with on-device speech recognition plus Gemma text condensation if native Gemma audio is unavailable.
- Do not overfit the demo to "ASL -> Nigerian Sign Language" language claims. Document the data honestly: ASL Citizen warm-start if used, custom Bassey recordings for the demo, community validation as future work unless a Deaf collaborator reviews it.
- Reduce recognition risk: start with hands + upper-body pose landmarks. Add face landmarks only if classifier quality needs it and device latency allows it.

## Scope Tiers

### Tier 0: Guaranteed Floor

- Emergency phrase grid.
- TTS speaks six high-value phrases.
- Offline badge and airplane-mode demo.
- Gemma rewrites selected phrases through ML Kit Prompt API or LiteRT-LM. A local sentence table may backstop temporary model errors, but it is not a valid submission path by itself.

### Tier 1: Demo MVP

- Camera preview and push-to-sign capture.
- Landmark extraction.
- Phrase classifier for the 25 locked phrases plus `unknown`.
- Top-3 picker and confidence threshold.
- Gemma sentence reconstruction through the selected in-app Gemma runtime: ML Kit Prompt API first, LiteRT-LM fallback.
- App-level function/tool trace for `detect_context`, `select_tone`, and `extract_intent`.
- Listen mode using on-device speech recognition plus Gemma condensation.

### Tier 2: Stretch

- Native Gemma audio input through an available runtime.
- Native Gemma function calling if Prompt API support lands before submission or if LiteRT-LM integration is feasible.
- ASL Citizen warm-start beyond a minimal experiment.

## Phase -1: Repo And Android Spike Scaffold

**Files:**
- Create: `.gitignore`
- Create: `LICENSE`
- Create: `README.md`
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`
- Create: `app/build.gradle.kts`
- Create: `app/src/main/AndroidManifest.xml`
- Create: `app/src/main/java/com/signbridge/MainActivity.kt`
- Create: `app/src/main/java/com/signbridge/spike/SpikeHomeScreen.kt`

- [ ] If the workspace is not already a git repo, run `git init` before creating implementation files.
- [ ] Add `.gitignore` covering Android build output, Gradle caches, local properties, keystores, ML datasets, raw captures, and model downloads.
- [ ] Add Apache 2.0 `LICENSE`.
- [ ] Add README skeleton with concrete headings for project pitch, hackathon track, bounded scope, privacy promise, setup, architecture, and known limitations.
- [ ] Scaffold the minimal single-module Gradle/Android project with Compose Material 3 and an empty `SpikeHomeScreen`.
- [ ] Add camera and microphone permissions only.
- [ ] Add only dependencies needed for Phase 0 runtime checks: Compose, ML Kit Prompt API, and ML Kit Speech Recognition. Add a documented Gradle version-catalog entry for LiteRT-LM but do not wire it into app dependencies until Phase 0 needs it.
- [ ] Build the debug app with `./gradlew :app:assembleDebug`.
- [ ] Install and launch the empty spike shell on emulator or device.
- [ ] Create the public GitHub repo early once GitHub auth is available; if auth is unavailable, keep the local repo clean and record the blocker in `docs/verification/gate-minus-1-bootstrap.md`.
- [ ] Commit the scaffold before Phase 0.

### Review Gate -1: Bootstrap Review

**Evidence file:** `docs/verification/gate-minus-1-bootstrap.md`

- [ ] Confirm the workspace is a git repo.
- [ ] Confirm `.gitignore`, README skeleton, Apache 2.0 license, and local git status are clean enough for a public repo.
- [ ] Confirm the Android debug app builds, installs, and launches.
- [ ] Confirm permissions are limited to camera and microphone.
- [ ] Decision: continue only if the spike scaffold can run on the target test device or emulator.

## Phase 0: API And Device Spike

**Files:**
- Create: `docs/verification/day-0-device-spike.md`
- Create: `docs/verification/litert-lm-spike.md`
- Create: `app/src/main/java/com/signbridge/ml/GemmaAvailability.kt`
- Create: `app/src/main/java/com/signbridge/spike/GemmaRuntimeSpike.kt`
- Modify: `app/src/main/java/com/signbridge/spike/SpikeHomeScreen.kt`

- [ ] Install or update Android AICore Beta and Google AI Edge Gallery on the S24 Ultra.
- [ ] In AI Edge Gallery, download Gemma 4 E2B and E4B, run one prompt online, then run one prompt in airplane mode.
- [ ] Join the AICore Developer Preview program and update AICore Beta.
- [ ] Build a tiny Android spike that calls ML Kit Prompt API with:
  - `ModelReleaseStage.PREVIEW` + `ModelPreference.FULL`
  - `ModelReleaseStage.PREVIEW` + `ModelPreference.FAST`
  - stable fallback
- [ ] Run two offline Gemma prompts in the spike for every available runtime: sign-gloss sentence reconstruction and hearing-reply condensation.
- [ ] Record `checkStatus()` results, first-token/total latency, total latency for both prompts, offline behavior after a cold launch, and error codes.
- [ ] If ML Kit Prompt API is unavailable, enable the LiteRT-LM dependency in `app/build.gradle.kts`, run `./gradlew :app:dependencies --configuration debugRuntimeClasspath`, and record the resolved LiteRT-LM version in `docs/verification/litert-lm-spike.md`.
- [ ] After enabling LiteRT-LM, run `./gradlew :app:assembleDebug` before writing the LiteRT-LM spike UI/code.
- [ ] If ML Kit Prompt API is unavailable for Gemma on the S24 Ultra, build a one-screen LiteRT-LM Kotlin spike with Gemma 4 E2B first, then E4B only if E2B works.
- [ ] Record LiteRT-LM model size, backend, first-token latency, total latency for both Gemma prompts, peak memory if available, and offline behavior.
- [ ] Add a speech spike using `com.google.mlkit:genai-speech-recognition:1.0.0-alpha1`.
- [ ] Record whether Basic and Advanced modes are `AVAILABLE`, `DOWNLOADABLE`, or `UNAVAILABLE` on the S24 Ultra.
- [ ] Decision gate:
  - If Preview FULL works offline: use E4B as default.
  - If only Preview FAST works: use E2B for the demo.
  - If Prompt API cannot access Gemma 4 from your app: attempt LiteRT-LM Gemma 4 E2B/E4B integration immediately.
  - If no Gemma runtime works in-app by the end of this gate: stop SignBridge as a Gemma 4 Good submission and rescope to a Gemma-accessible project before writing product code.
  - If Advanced speech is unavailable: use Basic/platform on-device speech recognition and use Gemma only to condense the transcript.

### Review Gate 0: API Reality Check

**Evidence file:** `docs/verification/gate-0-api-reality-check.md`

- [ ] Record exact S24 Ultra model, Android version, One UI version, AICore version, Google Play services version, and whether the bootloader is locked.
- [ ] Record Gemma 4 E2B/E4B AI Edge Gallery offline result.
- [ ] Record ML Kit Prompt API `checkStatus()` result for Preview FULL, Preview FAST, and Stable.
- [ ] Record Speech Recognition Basic and Advanced status.
- [ ] Decide the runtime path:
  - Preferred: Prompt API Preview FULL or FAST.
  - Fallback: Prompt API Stable.
  - Gemma fallback: LiteRT-LM Gemma 4 E2B/E4B.
  - Invalid for submission: local sentence templates without a working Gemma runtime.
- [ ] Confirm at least one in-app Gemma runtime can produce both the demo sentence transformation and a one-sentence hearing-reply condensation offline.
- [ ] Stop before Phase 1 if the runtime path is unknown or no Gemma runtime works in-app.

## Phase 1: Domain Core

**Files:**
- Create: `app/src/main/java/com/signbridge/navigation/AppNav.kt`
- Create: `app/src/main/java/com/signbridge/domain/PhraseCatalog.kt`
- Create: `app/src/main/java/com/signbridge/domain/Phrase.kt`
- Create: `app/src/main/java/com/signbridge/tts/TtsSpeaker.kt`
- Create: `app/src/test/java/com/signbridge/domain/PhraseCatalogTest.kt`
- Create: `app/src/test/java/com/signbridge/tts/TtsLocaleSelectorTest.kt`

- [ ] Confirm Phase -1 scaffold and Gate 0 runtime decision are complete before adding product domain code.
- [ ] RED: write `PhraseCatalogTest` asserting exactly 25 phrases, stable IDs 1-25, locked phrase text, categories, and six emergency grid phrases.
- [ ] Run `./gradlew testDebugUnitTest --tests "com.signbridge.domain.PhraseCatalogTest"` and verify it fails because `PhraseCatalog` does not exist.
- [ ] GREEN: implement `Phrase` and `PhraseCatalog` with only the locked phrase data needed to pass.
- [ ] Run the focused test and verify it passes.
- [ ] RED: write `TtsLocaleSelectorTest` for `en-NG` preferred, `en-US` fallback, and rate clamping.
- [ ] Run the focused TTS test and verify it fails because the selector does not exist.
- [ ] GREEN: implement the pure locale/rate selector used by `TtsSpeaker`.
- [ ] Run `./gradlew testDebugUnitTest` and verify all unit tests pass.
- [ ] Commit once the blank app launches and the domain/TTS tests pass.

### Review Gate 1: Foundation Review

**Evidence file:** `docs/verification/gate-1-foundation.md`

- [ ] Review changed files for accidental telemetry, extra permissions, or non-MVP dependencies.
- [ ] Confirm phrase IDs and text match PRD section 6 exactly.
- [ ] Confirm Phase -1 repo bootstrap remains intact.
- [ ] Confirm unit tests were observed red before green.
- [ ] Confirm app launches to a blank or basic shell on emulator/device.
- [ ] Decision: continue only if permissions, phrase catalog, and test harness are clean.

## Phase 2: Emergency Grid Hard Floor

**Files:**
- Create: `app/src/main/java/com/signbridge/ui/HomeScreen.kt`
- Create: `app/src/main/java/com/signbridge/ui/EmergencyScreen.kt`
- Create: `app/src/main/java/com/signbridge/ui/components/OfflineBadge.kt`
- Create: `app/src/main/java/com/signbridge/ui/components/PrimaryActionCard.kt`
- Create: `app/src/test/java/com/signbridge/emergency/EmergencyPhrasePresenterTest.kt`
- Create: `app/src/androidTest/java/com/signbridge/ui/EmergencyScreenTest.kt`
- Create: `docs/verification/test-android-apps-smoke-emergency.md`

- [ ] RED: write `EmergencyPhrasePresenterTest` proving the six emergency phrases are exposed in the required order and selecting one emits a speak command.
- [ ] Run the focused test and verify it fails because the presenter does not exist.
- [ ] GREEN: implement the emergency presenter/state logic without Compose.
- [ ] Run the focused test and verify it passes.
- [ ] Implement Home with three large actions: Sign to Speech, Listen, Emergency.
- [ ] Implement Emergency UI with six large phrase buttons using the tested presenter:
  - `Help me`
  - `I am Deaf`
  - `Please call emergency services`
  - `I am injured`
  - `Please write it down`
  - `I cannot hear you`
- [ ] RED: write a Compose UI test that taps an emergency phrase and verifies the fake speaker receives the selected phrase.
- [ ] Run the Compose test and verify it fails before the UI is wired to the presenter/fake speaker.
- [ ] GREEN: wire the UI to the presenter and `TtsSpeaker`.
- [ ] Add airplane-mode manual test evidence to `docs/verification/day-2-emergency-grid.md`.
- [ ] Run `./gradlew testDebugUnitTest connectedDebugAndroidTest` where a device/emulator is available.
- [ ] Run a light `test-android-apps:android-emulator-qa` smoke check: install debug build, launch app, navigate Home -> Emergency, tap one phrase, capture screenshot/UI tree/logcat, and save evidence to `docs/verification/test-android-apps-smoke-emergency.md`.
- [ ] Commit after the floor demo works without network.

### Review Gate 2: Hard Floor Demo Review

**Evidence file:** `docs/verification/gate-2-emergency-grid.md`

- [ ] Confirm the app can still provide value with no camera, no classifier, and no Gemma.
- [ ] Confirm airplane-mode TTS works for all six emergency phrases.
- [ ] Confirm Test Android Apps smoke evidence exists for the Emergency flow.
- [ ] Confirm no raw audio/video/file storage exists.
- [ ] Confirm UI is readable at large font settings.
- [ ] Decision: if this gate fails, repair before touching camera or ML.

## Phase 3: Camera, Landmarks, And Sign State

**Files:**
- Create: `app/src/main/java/com/signbridge/camera/CameraPreview.kt`
- Create: `app/src/main/java/com/signbridge/camera/FrameAnalyzer.kt`
- Create: `app/src/main/java/com/signbridge/landmarks/LandmarkExtractor.kt`
- Create: `app/src/main/java/com/signbridge/landmarks/LandmarkFrame.kt`
- Create: `app/src/main/java/com/signbridge/landmarks/LandmarkNormalizer.kt`
- Create: `app/src/main/java/com/signbridge/ui/SignToSpeechScreen.kt`
- Create: `app/src/main/java/com/signbridge/ui/debug/LandmarkOverlay.kt`
- Create: `app/src/test/java/com/signbridge/landmarks/LandmarkNormalizerTest.kt`
- Create: `app/src/test/java/com/signbridge/sign/SignCaptureStateTest.kt`
- Create: `docs/verification/test-android-apps-smoke-camera.md`

- [ ] RED: write `LandmarkNormalizerTest` for torso-centered normalization, missing-hand zero padding, and stable output shape.
- [ ] Run the focused test and verify it fails because `LandmarkNormalizer` does not exist.
- [ ] GREEN: implement `LandmarkFrame` and `LandmarkNormalizer`.
- [ ] Run the focused test and verify it passes.
- [ ] RED: write `SignCaptureStateTest` for idle -> recording -> processing -> result and reset transitions.
- [ ] Run the focused test and verify it fails because the state reducer does not exist.
- [ ] GREEN: implement the sign capture state reducer.
- [ ] Add CameraX front-camera preview at 720p target resolution.
- [ ] Wire push-to-sign UI to the tested state reducer.
- [ ] Run MediaPipe landmarks in the analyzer.
- [ ] Normalize landmarks by torso scale and center around shoulders/hips (root-landmark normalization relative to wrist/torso).
- [ ] Draw a debug overlay for hands and pose. Ensure both the Android runtime and the offline data extraction script use the exact same MediaPipe Hands + Pose extractor tasks (do not use the deprecated Holistic task) to prevent train/serve skew.
- [ ] Keep raw video in memory only. Do not persist frames.
- [ ] Run unit tests and a manual device check for preview, recording, and overlay.
- [ ] Run a light `test-android-apps:android-emulator-qa` smoke check for launch, camera permission screen/state, Sign to Speech navigation, UI tree, screenshot, and logcat. Save evidence to `docs/verification/test-android-apps-smoke-camera.md`.
- [ ] Commit when the S24 Ultra shows camera preview plus stable landmark overlay.

### Review Gate 3: Camera Privacy And Landmark Review

**Evidence file:** `docs/verification/gate-3-camera-landmarks.md`

- [ ] Confirm camera permission flow is understandable.
- [ ] Confirm no raw frames or videos are written to disk.
- [ ] Confirm Test Android Apps smoke evidence exists for Sign to Speech navigation and permission state.
- [ ] Confirm landmark tensors have the shape expected by the future classifier.
- [ ] Confirm preview remains responsive on S24 Ultra.
- [ ] Confirm tests were red/green for normalization and state transitions.
- [ ] Decision: if MediaPipe Holistic is too slow, cut to hands + pose before continuing.

## Phase 4: Data Collection And Dataset Format

**Files:**
- Create: `ml/phrase_map.json`
- Create: `ml/README.md`
- Create: `ml/DATASET_CARD.md`
- Create: `ml/fixtures/synthetic_landmarks.jsonl`
- Create: `ml/scripts/generate_synthetic_fixture.py`
- Create: `ml/scripts/validate_landmark_dataset.py`
- Create: `ml/scripts/split_dataset.py`
- Create: `ml/tests/test_generate_synthetic_fixture.py`
- Create: `ml/tests/test_dataset_card.py`
- Create: `ml/tests/test_validate_landmark_dataset.py`
- Create: `ml/tests/test_split_dataset.py`
- Create: `app/src/main/java/com/signbridge/data/CaptureSessionWriter.kt`

- [ ] RED: write `test_generate_synthetic_fixture.py` asserting the synthetic fixture contains all 26 labels, uses the same 30-frame tensor shape as real captures, and contains no camera/audio/person metadata.
- [ ] Run `pytest ml/tests/test_generate_synthetic_fixture.py -q` and verify it fails because the fixture generator does not exist.
- [ ] GREEN: implement `generate_synthetic_fixture.py` and create `ml/fixtures/synthetic_landmarks.jsonl`.
- [ ] RED: write `test_dataset_card.py` asserting `ml/DATASET_CARD.md` contains sections for consent, privacy, private captures, public synthetic fixture, phrase labels, and reviewer reproduction commands.
- [ ] Run `pytest ml/tests/test_dataset_card.py -q` and verify it fails because the dataset card does not exist.
- [ ] Add `ml/DATASET_CARD.md` explaining private capture policy, consent posture, exact phrase labels, synthetic fixture purpose, and how reviewers can run tests without private recordings. Include a section documenting the Lanfrica NSL dataset license (Apache-2.0 compatibility), signer demographics, consent posture, and which subset is used for representation learning.
- [ ] Run `pytest ml/tests/test_dataset_card.py -q` and verify it passes.
- [ ] RED: write Python tests for dataset validation: rejects missing phrases, rejects wrong frame count, accepts 25 phrases + unknown, and rejects raw video files in the dataset folder.
- [ ] Run `pytest ml/tests/test_validate_landmark_dataset.py -q` and verify it fails because the validator does not exist.
- [ ] GREEN: implement `validate_landmark_dataset.py`.
- [ ] RED: write Python tests for held-out-take splitting so clips from the same take never appear in both train and validation.
- [ ] Run `pytest ml/tests/test_split_dataset.py -q` and verify it fails because the splitter does not exist.
- [ ] GREEN: implement `split_dataset.py`.
- [ ] RED: write a JVM test for `CaptureSessionWriter` metadata serialization without video paths.
- [ ] Run the focused JVM test and verify it fails because the writer does not exist.
- [ ] GREEN: add debug-only capture that writes landmark windows, phrase label, take number, lighting tag, and timestamp.
- [ ] Ensure captures store landmarks only, not raw video.
- [ ] Record 25 phrases x 10 takes, plus at least 50 `unknown`/non-sign windows.
- [ ] Split by held-out take number, not random frames.
- [ ] Run `validate_landmark_dataset.py` to verify every phrase has 10 takes and `unknown` exists.
- [ ] Back up the dataset outside git.
- [ ] Commit code, dataset schema, dataset card, and synthetic fixture. Do not commit personal raw captures or private landmark captures unless intentionally released with consent.

### Review Gate 4: Dataset Ethics And Quality Review

**Evidence file:** `docs/verification/gate-4-dataset.md`

- [ ] Confirm no raw video/audio was committed.
- [ ] Confirm `ml/fixtures/synthetic_landmarks.jsonl` and `ml/DATASET_CARD.md` let public reviewers run validation/training smoke tests without private captures.
- [ ] Confirm consent/data contribution defaults remain off.
- [ ] Confirm phrase coverage, take counts, and unknown examples.
- [ ] Confirm train/validation split is by take, not frame leakage.
- [ ] Decision: if data quality is uneven, collect more takes before training.

## Phase 5: Classifier Training

**Files:**
- Create: `ml/signbridge_model.py`
- Create: `ml/train_classifier.py`
- Create: `ml/export_tflite.py`
- Create: `ml/evaluate_classifier.py`
- Create: `ml/export_metadata.py`
- Create: `ml/tests/test_model_shapes.py`
- Create: `ml/tests/test_evaluate_classifier.py`
- Create: `ml/tests/test_tflite_export_contract.py`
- Create: `ml/scripts/pretrain_on_asl.py`
- Create: `ml/models/README.md`
- Create: `ml/MODEL_CARD.md`
- Create: `docs/verification/classifier-report.md`

- [ ] RED: write tests that assert the Keras model input shape is `[None, 30, LANDMARK_DIM]`, output shape is `[None, 26]`, and class count is `26` including `unknown`.
- [ ] Run `pytest ml/tests/test_model_shapes.py -q` and verify it fails because the training model does not exist.
- [ ] GREEN: implement the smallest TensorFlow/Keras temporal baseline in `ml/signbridge_model.py` that passes shape tests.
- [ ] RED: write evaluator tests for top-1, top-3, confusion matrix, and per-class recall from fixed fake logits.
- [ ] Run evaluator tests and verify they fail because evaluator functions do not exist.
- [ ] GREEN: implement evaluator functions.
- [ ] RED: write `test_tflite_export_contract.py` proving export produces:
  - `ml/models/signbridge_phrases_v1.tflite`
  - `ml/models/signbridge_phrases_v1.labels.json`
  - input tensor `[1, 30, LANDMARK_DIM]` with `float32`
  - output tensor `[1, 26]` with `float32`
  - identical label ordering to `ml/phrase_map.json`
- [ ] Run the export contract test and verify it fails because export code does not exist.
- [ ] GREEN: implement `export_tflite.py` using `tf.lite.TFLiteConverter.from_saved_model` or `from_keras_model`.
- [ ] Export float32 TFLite first. Do not int8-quantize until float32 parity passes.
- [ ] Add `export_metadata.py` to write label mapping, input shape, normalization version, model version, and training dataset hash.
- [ ] Run a training/export smoke test on `ml/fixtures/synthetic_landmarks.jsonl` so public reviewers can verify the pipeline without private captures.
- [ ] Implement the SOTA architecture in `ml/signbridge_model.py`: a 1D-CNN Stem -> Transformer Blocks (Self-Attention) -> ECA (Efficient Channel Attention) over a fixed `WINDOW_FRAMES = 30` input length. Keep the final classification head at 26 classes (25 phrases + `unknown`).
- [ ] Use Lanfrica NSL and ASL Citizen datasets strictly for representation-learning warm-starts via `ml/scripts/pretrain_on_asl.py`. Fine-tune the final 26-class head on custom captures. Train the `unknown` class explicitly with negative examples.
- [ ] Apply augmentations strictly in this order: time-warp -> spatial affine -> finger dropout.
- [ ] Export `signbridge_phrases_v1.tflite` and `signbridge_phrases_v1.labels.json`.
- [ ] Validate TFLite parity by running the Keras model and the TFLite interpreter on at least 20 held-out windows; require matching top-1 for at least 19/20 and matching top-3 for 20/20 before Android integration.
- [ ] Optional only after float32 parity: export dynamic-range quantized TFLite and keep it only if top-3 parity and on-device latency improve.
- [ ] Evaluate top-1, top-3, confusion matrix, and per-class recall.
- [ ] Perform bias evaluation and confusion matrix slices by lighting tag and signer.
- [ ] Write `ml/MODEL_CARD.md` detailing intended use, out-of-scope use, training summary, evaluation slices, and known failure modes. Add a single-signer limitation banner.
- [ ] Record model size (<40MB limit), first-inference latency, and steady-state inference latency on target hardware.
- [ ] Gate:
  - If top-1 >= 85% on your held-out takes AND float32 parity holds: continue with 25 phrases.
  - If top-1 is 70-84%: keep 25 phrases but rely on top-3 picker.
  - If top-1 < 70% by end of Day 8 or if latency/size budget is blown: cut live recognition to the 15 strongest phrases and keep the full emergency/manual grid.
- [ ] Commit training scripts, report, model card, and exported model metadata.

### Review Gate 5: Classifier Evidence Review

**Evidence file:** `docs/verification/gate-5-classifier.md`

- [ ] Review metrics against the cut-line thresholds.
- [ ] Inspect confusion matrix for demo-critical phrases: `I am Deaf`, `Please calm down`, `It was an accident`, `My brakes failed`, `I am not trying to fight`.
- [ ] Confirm exported model metadata matches `PhraseCatalog` IDs.
- [ ] Confirm synthetic-fixture training/export smoke evidence is in `docs/verification/classifier-report.md`.
- [ ] Confirm Keras -> TFLite parity evidence is in `docs/verification/classifier-report.md`.
- [ ] Confirm Android input tensor shape and dtype match the exported metadata before Phase 6.
- [ ] Confirm inference target is under the latency budget on device or emulator.
- [ ] Decision: continue with 25, continue with top-3 fallback, or cut live recognition to strongest 15.

## Phase 6: Classifier Integration

**Files:**
- Create: `app/src/main/assets/signbridge_phrases_v1.tflite`
- Create: `app/src/main/assets/signbridge_phrases_v1.labels.json`
- Create: `app/src/main/java/com/signbridge/ml/SlidingWindowBuffer.kt`
- Create: `app/src/main/java/com/signbridge/ml/SignClassifier.kt`
- Create: `app/src/main/java/com/signbridge/ml/ClassificationResult.kt`
- Create: `app/src/main/java/com/signbridge/ml/ExponentialSmoother.kt`
- Create: `app/src/test/java/com/signbridge/ml/SlidingWindowBufferTest.kt`
- Create: `app/src/test/java/com/signbridge/ml/SignClassifierTensorContractTest.kt`
- Create: `app/src/test/java/com/signbridge/ml/ExponentialSmootherTest.kt`

- [ ] RED: write `SlidingWindowBufferTest` for 30-frame readiness, reset behavior, overflow behavior, and stable tensor ordering.
- [ ] Run the focused test and verify it fails because `SlidingWindowBuffer` does not exist.
- [ ] GREEN: implement `SlidingWindowBuffer`.
- [ ] RED: write classifier mapping tests using a fake interpreter so top-3 labels and confidence order are correct.
- [ ] Run the focused mapping test and verify it fails because `SignClassifier` does not exist.
- [ ] GREEN: implement classifier wrapper/mapping around LiteRT/TFLite.
- [ ] RED: write `SignClassifierTensorContractTest` asserting asset label IDs match `PhraseCatalog`, input shape is 30 frames, and output class count is 26.
- [ ] Run the tensor contract test and verify it fails before assets/metadata are wired.
- [ ] RED: write `ExponentialSmootherTest` proving constant-input yields constant-output, and step-change yields a bounded transient prediction curve.
- [ ] GREEN: implement `ExponentialSmoother` to prevent UI flicker on live predictions.
- [ ] GREEN: copy the exported `.tflite` and `.labels.json` into app assets and wire metadata loading.
- [ ] Run inference after the user releases push-to-sign. Apply the `ExponentialSmoother` to the live confidence scores.
- [ ] Display top-3 predictions and confidence.
- [ ] Never auto-speak below 0.65 confidence.
- [ ] Run unit tests plus one manual on-device prediction pass.
- [ ] Commit when predictions show in the Sign to Speech screen on-device.

### Review Gate 6: Sign-To-Prediction Review

**Evidence file:** `docs/verification/gate-6-classifier-integration.md`

- [ ] Confirm low-confidence cases do not auto-speak.
- [ ] Confirm top-3 picker is usable.
- [ ] Confirm no stale frames survive after reset.
- [ ] Confirm phrase IDs are consistent from model output to UI.
- [ ] Decision: continue only if demo-critical phrases can be selected reliably, even if top-1 is imperfect.

## Phase 7: Gemma Reconstruction And Tool Trace

**Files:**
- Create: `app/src/main/java/com/signbridge/gemma/GemmaClient.kt`
- Create: `app/src/main/java/com/signbridge/gemma/MlKitGemmaClient.kt`
- Create: `app/src/main/java/com/signbridge/gemma/LiteRtGemmaClient.kt`
- Create: `app/src/main/java/com/signbridge/gemma/FakeGemmaClient.kt`
- Create: `app/src/main/java/com/signbridge/gemma/GemmaRuntimeSelector.kt`
- Create: `app/src/main/java/com/signbridge/gemma/GemmaPromptBuilder.kt`
- Create: `app/src/main/java/com/signbridge/gemma/TranslationJson.kt`
- Create: `app/src/main/java/com/signbridge/tools/SignBridgeTools.kt`
- Create: `docs/verification/gemma-tooling-claim.md`
- Create: `app/src/test/java/com/signbridge/tools/SignBridgeToolsTest.kt`
- Create: `app/src/test/java/com/signbridge/gemma/GemmaPromptBuilderTest.kt`
- Create: `app/src/test/java/com/signbridge/gemma/TranslationJsonTest.kt`
- Create: `app/src/test/java/com/signbridge/gemma/GemmaModelSelectionTest.kt`
- Create: `app/src/test/java/com/signbridge/gemma/GemmaRuntimeSelectorTest.kt`
- Create: `app/src/test/java/com/signbridge/gemma/LiteRtGemmaClientConfigTest.kt`

- [ ] RED: write `SignBridgeToolsTest` for:
  - `detect_context(glosses, timeOfDay)` returns roadside, clinic, retail, or general.
  - `select_tone(context, urgency)` returns calm-deescalating, neutral, or urgent.
  - `extract_intent(transcript)` returns agreeing, asking, refusing, or explaining.
- [ ] Run the tools test and verify it fails because the tools do not exist.
- [ ] GREEN: implement `SignBridgeTools`.
- [ ] RED: write `GemmaPromptBuilderTest` proving the prompt includes glosses, tool trace, no-added-facts rule, JSON schema, and Nigerian-English de-escalation guidance.
- [ ] Run the prompt test and verify it fails because the builder does not exist.
- [ ] GREEN: implement `GemmaPromptBuilder`.
- [ ] RED: write `TranslationJsonTest` for valid JSON, malformed JSON fallback, missing fields fallback, and `needs_confirmation` mapping.
- [ ] Run the JSON test and verify it fails because parser/fallback does not exist.
- [ ] GREEN: implement `TranslationJson` parser and deterministic sentence fallback.
- [ ] Implement `GemmaClient` as an interface so the UI can be tested without AICore.
- [ ] RED: write `GemmaModelSelectionTest` for ML Kit Preview FULL, Preview FAST, Stable fallback, and unavailable-model fallback copy.
- [ ] Run the model selection test and verify it fails because the selection policy does not exist.
- [ ] GREEN: implement the ML Kit model selection policy used by `MlKitGemmaClient`.
- [ ] RED: write `GemmaRuntimeSelectorTest` proving the app uses the Gate 0 runtime decision: ML Kit when available, LiteRT-LM when ML Kit is unavailable, and hard-stop copy when neither is available.
- [ ] Run the runtime selector test and verify it fails because `GemmaRuntimeSelector` does not exist.
- [ ] GREEN: implement `GemmaRuntimeSelector`.
- [ ] RED: write `LiteRtGemmaClientConfigTest` proving the LiteRT-LM client receives the selected model path, backend preference, max output tokens, and offline-only configuration.
- [ ] Run the LiteRT-LM config test and verify it fails because `LiteRtGemmaClient` does not exist.
- [ ] GREEN: implement `LiteRtGemmaClient` behind `GemmaClient` using the LiteRT-LM Kotlin API selected in Gate 0.
- [ ] Implement ML Kit Prompt API wiring with the tested model selection policy.
- [ ] Wire app runtime selection so sentence reconstruction and reply condensation both call the same selected `GemmaClient` implementation.
- [ ] Check whether the selected Gemma runtime supports native function calling in-app. Record the exact API/runtime evidence in `docs/verification/gemma-tooling-claim.md`.
- [ ] If native function calling is unavailable, label the feature in README/WRITEUP/video as "app-level tool orchestration around Gemma output", not "native Gemma function calling".
- [ ] If native function calling is available, add a separate native-tool-call test path before claiming it.
- [ ] Run all Gemma/tool tests.
- [ ] Commit after the app can convert `["I am Deaf", "It was an accident", "My brakes failed"]` into the demo sentence offline.

### Review Gate 7: Gemma Behavior Review

**Evidence file:** `docs/verification/gate-7-gemma-reconstruction.md`

- [ ] Confirm prompt output never adds facts beyond selected glosses in at least 20 test cases.
- [ ] Confirm malformed Gemma output falls back gracefully.
- [ ] Confirm tool trace is visible in code/write-up/debug evidence.
- [ ] Confirm `docs/verification/gemma-tooling-claim.md` states whether the submission uses native Gemma function calling or app-level tool orchestration, with no overclaim.
- [ ] Confirm runtime selection follows Gate 0 decision.
- [ ] Confirm both sentence reconstruction and reply condensation use the selected runtime: ML Kit or LiteRT-LM, not separate unverified paths.
- [ ] Decision: if Prompt API is flaky, use deterministic fallback for the demo and document Gemma limitation honestly.

## Phase 8: Listen Mode

**Files:**
- Create: `app/src/main/java/com/signbridge/speech/SpeechToTextClient.kt`
- Create: `app/src/main/java/com/signbridge/speech/MlKitSpeechToTextClient.kt`
- Create: `app/src/main/java/com/signbridge/speech/ReplyCondenser.kt`
- Create: `app/src/main/java/com/signbridge/ui/ListenScreen.kt`
- Create: `app/src/test/java/com/signbridge/speech/ReplyCondenserTest.kt`
- Create: `app/src/test/java/com/signbridge/speech/ListenStateTest.kt`

- [ ] RED: write `ReplyCondenserTest` proving the prompt preserves key facts, limits output to one sentence, and refuses to add information.
- [ ] Run the focused test and verify it fails because `ReplyCondenser` does not exist.
- [ ] GREEN: implement `ReplyCondenser` prompt builder and parser/fallback.
- [ ] RED: write Listen state tests for idle -> recording -> transcribing -> result and typed-reply fallback.
- [ ] Run the focused test and verify it fails because Listen state does not exist.
- [ ] GREEN: implement Listen state reducer.
- [ ] Implement hold-to-talk UI.
- [ ] Use ML Kit Speech Recognition Basic if Advanced is unavailable.
- [ ] Pipe transcript text into the selected `GemmaClient` runtime with: "Condense to one sentence. Preserve key facts. Do not add information."
- [ ] Run `extract_intent` on transcript and show intent discreetly in debug mode/write-up, not as extra user clutter.
- [ ] If speech recognition is unavailable offline, provide a "type reply" fallback.
- [ ] Run unit tests and one manual offline Listen-mode check.
- [ ] Commit once hearing-person speech appears as large text.

### Review Gate 8: Bidirectional Flow Review

**Evidence file:** `docs/verification/gate-8-listen-mode.md`

- [ ] Confirm Listen mode works offline through the selected speech path, or typed fallback is ready.
- [ ] Confirm large transcript text is readable at 32sp minimum.
- [ ] Confirm Gemma condensation preserves facts and does not invent agreement/refusal.
- [ ] Decision: keep reverse loop, cut to typed fallback, or drop reverse loop from the video.

## Phase 9: UX, Accessibility, And Settings

**Files:**
- Create: `app/src/main/java/com/signbridge/ui/SettingsScreen.kt`
- Create: `app/src/main/java/com/signbridge/ui/OnboardingScreen.kt`
- Create: `app/src/main/java/com/signbridge/settings/AppSettings.kt`
- Create: `app/src/main/java/com/signbridge/settings/SettingsStore.kt`
- Create: `app/src/test/java/com/signbridge/settings/AppSettingsTest.kt`
- Create: `app/src/test/java/com/signbridge/onboarding/DisclaimerStateTest.kt`

- [ ] RED: write `AppSettingsTest` for auto-speak off by default, confidence threshold default 0.65, model preference fallback, data contribution off by default, and voice rate bounds.
- [ ] Run the focused settings test and verify it fails because settings models do not exist.
- [ ] GREEN: implement `AppSettings` and `SettingsStore`.
- [ ] RED: write onboarding/disclaimer state test proving the disclaimer is shown before first use and can be acknowledged.
- [ ] Run the focused test and verify it fails because onboarding state does not exist.
- [ ] GREEN: implement disclaimer onboarding state.
- [ ] Add disclaimer onboarding UI.
- [ ] Add settings UI: auto-speak toggle, voice rate, confidence threshold, model preference, data contribution off by default.
- [ ] Support large text and high contrast.
- [ ] Test with Android font scaling.
- [ ] Commit after accessibility pass.

### Review Gate 9: UX And Safety Review

**Evidence file:** `docs/verification/gate-9-ux-safety.md`

- [ ] Confirm no shame states: use "Please repeat" or "Pick the right phrase", not "Recognition failed".
- [ ] Confirm auto-speak is opt-in and blocked below threshold.
- [ ] Confirm disclaimer is present and honest.
- [ ] Confirm settings defaults match privacy-first requirements.
- [ ] Decision: repair any safety/privacy issue before release candidate work.

## Phase 10: Offline And Failure-Mode QA

**Files:**
- Create: `docs/verification/offline-test-matrix.md`
- Create: `docs/verification/performance-benchmarks.md`
- Create: `docs/verification/physical-s24-qa.md`
- Create: `docs/verification/test-android-apps-qa.md`

- [ ] Test fresh app launch in airplane mode after models are downloaded.
- [ ] Test missing camera permission.
- [ ] Test missing mic permission.
- [ ] Test low-confidence sign.
- [ ] Test no hands detected.
- [ ] Test AICore unavailable.
- [ ] Test TTS unavailable or unsupported `en-NG`.
- [ ] Record physical S24 Ultra QA evidence in `docs/verification/physical-s24-qa.md`: install path, airplane-mode cold launch, exact demo flow, screenshots/video references, and observed failures.
- [ ] Use `test-android-apps:android-emulator-qa` workflow for emulator/plugin QA: install build, launch app with adb, inspect UI tree, capture screenshots, and collect logcat. Save this separately in `docs/verification/test-android-apps-qa.md`.
- [ ] Record latency, RAM observations, model sizes, and known limitations.
- [ ] Commit verification docs.

### Review Gate 10: Offline Release Readiness

**Evidence file:** `docs/verification/gate-10-offline-readiness.md`

- [ ] Confirm airplane-mode cold launch works after required models are already downloaded.
- [ ] Confirm all expected failures degrade to usable fallbacks.
- [ ] Confirm no network permission was added.
- [ ] Confirm performance evidence is documented.
- [ ] Confirm physical S24 Ultra QA evidence exists in `docs/verification/physical-s24-qa.md`.
- [ ] Confirm Test Android Apps QA evidence exists in `docs/verification/test-android-apps-qa.md`.
- [ ] Decision: only move to release candidate if the demo path works three times consecutively.

## Phase 11: Release Candidate

**Files:**
- Create: `docs/adr/003-float32-vs-int8.md`
- Create: `docs/release/apk-signing.md`
- Modify: `README.md`
- Create: `WRITEUP.md`

- [ ] Write three short ADRs for ML Kit vs LiteRT-LM, hands+pose vs Holistic, and float32 vs int8.
- [ ] Generate release keystore and document where it is stored outside git.
- [ ] Draft `docs/privacy.md` and threat model detailing what stays on device, what leaves, and what is logged.
- [ ] Update `WRITEUP.md` with Lanfrica NSL dataset attribution.
- [ ] Build signed APK.
- [ ] Install APK from scratch on S24 Ultra.
- [ ] Run the exact demo path three times on the physical S24 Ultra and update `docs/verification/physical-s24-qa.md`.
- [ ] Run the Test Android Apps emulator/device QA flow again against the release candidate APK and update `docs/verification/test-android-apps-qa.md`.
- [ ] Draft README with architecture, privacy, setup, screenshots, and known limitations.
- [ ] Draft WRITEUP with Gemma usage, native-vs-app-level tool claim, tool trace, offline behavior, benchmarks, and ethics.
- [ ] Commit release docs and write-up.

### Review Gate 11: Submission Artifact Review

**Evidence file:** `docs/verification/gate-11-submission-artifacts.md`

- [ ] Confirm README, WRITEUP, app UI, and video script tell the same bounded story.
- [ ] Confirm README/WRITEUP/video wording matches `docs/verification/gemma-tooling-claim.md`.
- [ ] Confirm APK installs from scratch.
- [ ] Confirm physical S24 Ultra QA passed on the release candidate or document physical-device blockers.
- [ ] Confirm Test Android Apps QA passed on the release candidate or document device/emulator blockers.
- [ ] Confirm public repo contains no secrets, keystore, personal raw data, or private recordings.
- [ ] Confirm known limitations are explicit.
- [ ] Decision: repair artifacts before shooting final or submitting.

## Phase 12: Video And Submission

**Files:**
- Create: `docs/submission/video-script.md`
- Create: `docs/submission/kaggle-checklist.md`

- [ ] Shoot only the Lagos roadside scenario.
- [ ] Show airplane mode visibly.
- [ ] Capture close-ups of recognition confidence, Gemma output, TTS speak action, and listen mode.
- [ ] Add captions throughout.
- [ ] Keep final video under 3 minutes.
- [ ] Upload public repo, write-up, video, cover image, and APK link.
- [ ] Submit by May 17, 2026, 23:59 UTC.

### Review Gate 12: Final Submission Review

**Evidence file:** `docs/verification/gate-12-final-submission.md`

- [ ] Confirm Kaggle submission form has repo, video, write-up, cover image, and APK link.
- [ ] Confirm video captions are present throughout.
- [ ] Confirm deadline buffer remains intact.
- [ ] Confirm the submitted description does not overclaim universal sign-language translation.
- [ ] Decision: submit, or cut nonessential claims/assets and submit before the buffer closes.

## Validation Metrics

- Phrase classifier top-1 and top-3 accuracy on held-out takes.
- Median sign recognition latency after release of push-to-sign.
- Gemma reconstruction latency for demo phrase.
- Speech-to-text latency for one hearing reply.
- TTS time-to-speech after confirmation tap.
- Airplane-mode success from cold app launch after model download.

## Cut Lines

1. If AICore Prompt API is blocked, use LiteRT-LM Gemma 4 E2B/E4B. If no Gemma runtime works in-app, stop SignBridge as a Gemma 4 Good submission and rescope before building further.
2. If speech recognition fails offline, cut reverse loop to "type reply" plus Gemma condensation.
3. If classifier accuracy is weak, reduce live recognition to 15 strongest phrases and keep all 25 in manual/emergency mode.
4. If MediaPipe Holistic is too slow, use hands + pose only.
5. If video time is at risk, skip advanced reverse loop and make the emergency grid plus one sign-to-speech path perfect.

## Source Notes

- Kaggle competition page: https://www.kaggle.com/competitions/gemma-4-good-hackathon
- Medium overview by Sudha Rani Maddala/Kaggle Team: https://sudhamsr.medium.com/the-gemma-4-good-hackathon-aef927f17ef1
- Android Gemma 4 AICore Developer Preview: https://developer.android.com/blog/posts/announcing-gemma-4-in-the-ai-core-developer-preview
- ML Kit Prompt API: https://developers.google.com/ml-kit/genai/prompt/android/get-started
- AICore Developer Preview guide: https://developers.google.com/ml-kit/genai/aicore-dev-preview
- ML Kit model selection: https://developers.google.com/ml-kit/genai/prompt/android/select-model
- ML Kit GenAI Speech Recognition: https://developers.google.com/ml-kit/genai/speech-recognition/android
- Gemma 4 function calling: https://ai.google.dev/gemma/docs/capabilities/text/function-calling-gemma4
- Gemma 4 audio: https://ai.google.dev/gemma/docs/capabilities/audio
- LiteRT-LM overview: https://ai.google.dev/edge/litert-lm/overview
