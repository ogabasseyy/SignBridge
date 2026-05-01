# Classifier Report

Date: 2026-05-01

## Status

Contract pipeline implemented; a valid untrained TFLite contract model now exports locally. Real classifier training still requires private landmark captures.

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

## Local Export Result

TensorFlow 2.21.0 was installed into the local Python 3.13 virtual environment and `ml/export_tflite.py` exported a valid float32 TFLite model with backend `tensorflow_tflite_converter`.

The exported model is an untrained architecture/shape contract generated from the baseline Keras model. It is not a production classifier and must not be represented as real sign recognition. The real training step should run on the private landmark captures, then replace this contract model only after held-out accuracy and Keras/TFLite parity pass.

## Verification

- `.venv/bin/pytest ml/tests/test_model_shapes.py ml/tests/test_evaluate_classifier.py ml/tests/test_tflite_export_contract.py -q`
- `.venv/bin/python ml/export_tflite.py --fixture ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json --model-output ml/models/signbridge_phrases_v1.tflite --labels-output ml/models/signbridge_phrases_v1.labels.json --metadata-output ml/models/signbridge_phrases_v1.metadata.json`
- `.venv/bin/python ml/train_classifier.py --dataset ml/fixtures/synthetic_landmarks.jsonl --phrase-map ml/phrase_map.json --output-dir ml/models`

## Next Real Training Gate

- Train on private landmark captures split by held-out take.
- Export float32 TFLite through `tf.lite.TFLiteConverter`.
- Validate Keras/TFLite parity on held-out windows.
- Replace the untrained contract artifact only after parity passes.
