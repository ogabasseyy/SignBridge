# SignBridge Dataset Card

## Consent

Hackathon private captures must come only from people who explicitly agree to record landmark-only signing examples for this project. Any future community collection needs a separate consent form, withdrawal path, and review by Deaf community contributors.

## Privacy

The public repository does not include raw video, raw audio, face images, names, phone numbers, locations, or private capture metadata. The Android app processes camera frames in memory and the public ML tools validate landmark tensors only.

## Private Captures

Private captures live outside git under ignored paths such as `ml/private/`, `ml/captures/`, or external storage. A private capture record may contain phrase label, take number, timestamp, lighting tag, and normalized landmark windows. It must not contain raw video paths or personal identifiers.

## Public Synthetic Fixture

`ml/fixtures/synthetic_landmarks.jsonl` is a deterministic fixture for reviewer reproduction. It is not training data and should not be represented as Deaf community signing data. Its purpose is to prove schema, validation, splitting, and export smoke tests without exposing private recordings.

## Phrase Labels

The label set is defined in `ml/phrase_map.json`. It contains the 25 locked PRD phrases plus `unknown`, for 26 classifier outputs total.

## Reviewer Reproduction Commands

```bash
python3 -m venv .venv
.venv/bin/python -m pip install pytest
.venv/bin/pytest ml/tests -q
python ml/scripts/generate_synthetic_fixture.py --output ml/fixtures/synthetic_landmarks.jsonl
python ml/scripts/validate_landmark_dataset.py --dataset ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json
```
