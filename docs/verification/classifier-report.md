# Classifier Report

Date: 2026-05-01

## Status

Contract pipeline implemented; real classifier training still requires a TensorFlow-enabled environment and private landmark captures.

## Implemented

- Model shape contract:
  - input: `[None, 30, 1629]`
  - output: `[None, 26]`
  - labels: 25 locked phrases plus `unknown`
- Pure evaluator helpers:
  - top-k accuracy
  - confusion matrix
- TFLite export contract:
  - `ml/models/signbridge_phrases_v1.tflite`
  - `ml/models/signbridge_phrases_v1.labels.json`
  - `ml/models/signbridge_phrases_v1.metadata.json`
- Metadata includes:
  - input/output shape
  - dtype
  - label count
  - normalization version
  - dataset SHA-256

## Local Limitation

The local Python 3.13 virtual environment does not have TensorFlow installed. `ml/export_tflite.py` therefore wrote a deterministic placeholder `.tflite` artifact with backend `placeholder_no_tensorflow`.

This placeholder is not a production classifier and must not be represented as real sign recognition. The real training/export step should run in Kaggle or another TensorFlow environment, then replace the placeholder `.tflite` before Android classifier integration.

## Verification

- `.venv/bin/pytest ml/tests/test_model_shapes.py ml/tests/test_evaluate_classifier.py ml/tests/test_tflite_export_contract.py -q`
- `.venv/bin/python ml/export_tflite.py --fixture ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json --model-output ml/models/signbridge_phrases_v1.tflite --labels-output ml/models/signbridge_phrases_v1.labels.json --metadata-output ml/models/signbridge_phrases_v1.metadata.json`
- `.venv/bin/python ml/train_classifier.py --dataset ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json --output-dir ml/models`

## Next Real Training Gate

- Install TensorFlow in Kaggle.
- Train on private landmark captures split by held-out take.
- Export float32 TFLite through `tf.lite.TFLiteConverter`.
- Validate Keras/TFLite parity on held-out windows.
- Replace placeholder artifact only after parity passes.
