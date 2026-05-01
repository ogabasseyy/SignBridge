# Gate 4 Dataset Ethics And Quality Review

Date: 2026-05-01

## Result

Passed for public dataset tooling.

Private real captures are still pending and must stay outside git unless intentionally released with consent.

## Review

- Public `ml/phrase_map.json` contains 25 locked phrases plus `unknown`.
- Public synthetic fixture exists at `ml/fixtures/synthetic_landmarks.jsonl`.
- Synthetic fixture contains:
  - 26 labels
  - 30 frames per row
  - 1629 float values per frame
  - no camera/audio/person/video metadata
- `ml/DATASET_CARD.md` documents:
  - consent
  - privacy
  - private captures
  - public synthetic fixture
  - phrase labels
  - reviewer reproduction commands
- Validator rejects:
  - missing labels
  - wrong frame count
  - raw video files in the dataset folder
  - forbidden public metadata keys
- Splitter holds out records by take number, not random frames.
- Android `CaptureSessionWriter` serializes capture metadata without raw media paths.

## TDD Evidence

RED:

- Python tests failed before `ml.scripts` and dataset artifacts existed.
- `CaptureSessionWriterTest` failed before `CaptureSessionWriter` existed.

GREEN:

- `.venv/bin/pytest ml/tests -q` passed.
- `./gradlew testDebugUnitTest :app:assembleDebug` passed.

## Verification Commands

- `.venv/bin/pytest ml/tests -q`
- `.venv/bin/python ml/scripts/generate_synthetic_fixture.py --output ml/fixtures/synthetic_landmarks.jsonl`
- `.venv/bin/python ml/scripts/validate_landmark_dataset.py --dataset ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json`
- `.venv/bin/python ml/scripts/split_dataset.py --dataset ml/fixtures/synthetic_landmarks.jsonl --validation-takes 0 --train-output /tmp/signbridge-train.jsonl --validation-output /tmp/signbridge-validation.jsonl`
- `./gradlew testDebugUnitTest :app:assembleDebug`

## Remaining Private Data Work

- Record 25 phrases x 10 takes plus unknown/non-sign windows on the target phone.
- Validate private captures with `validate_landmark_dataset.py`.
- Split by held-out take number.
- Back up private captures outside git.
