# SignBridge ML

This folder contains the public, reproducible ML data tooling for SignBridge.

Private signing captures are not committed. Reviewers can run the synthetic fixture, validation, split, and training smoke tests without access to private recordings.

## Commands

```bash
.venv/bin/pytest ml/tests -q
python ml/scripts/generate_synthetic_fixture.py --output ml/fixtures/synthetic_landmarks.jsonl
python ml/scripts/validate_landmark_dataset.py --dataset ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json
python ml/scripts/split_dataset.py --dataset ml/fixtures/synthetic_landmarks.jsonl --validation-takes 0 --train-output /tmp/signbridge-train.jsonl --validation-output /tmp/signbridge-validation.jsonl
```
