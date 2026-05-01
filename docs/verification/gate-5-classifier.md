# Gate 5 Classifier Evidence Review

Date: 2026-05-01

## Result

Partially passed.

The classifier contract, labels, evaluator, metadata, and export path are implemented. A real trained classifier is still pending because the local environment has no TensorFlow and no private capture dataset yet.

## Review

- Android tensor shape is locked at 30 frames x 1629 floats.
- Output class count is 26: 25 locked phrases plus `unknown`.
- `ml/models/signbridge_phrases_v1.metadata.json` matches the Android tensor contract.
- `ml/models/signbridge_phrases_v1.labels.json` matches `ml/phrase_map.json`.
- Evaluation helpers cover top-k accuracy and confusion matrix.
- Synthetic-fixture export smoke is documented in `docs/verification/classifier-report.md`.
- Keras -> TFLite parity is not available yet. It must be produced in the TensorFlow/Kaggle environment before replacing the placeholder model.

## Decision

Continue with top-3 fallback UI using the placeholder interpreter locally. Do not claim real sign recognition until the TensorFlow-trained TFLite model replaces the placeholder and parity evidence exists.
