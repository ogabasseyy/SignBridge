# SignBridge Models

Generated classifier artifacts belong here when produced by `ml/export_tflite.py`.

The local Codex environment does not currently have TensorFlow installed, so `export_tflite.py` writes a deterministic placeholder `.tflite` contract artifact unless TensorFlow is available. A Kaggle or local training environment with TensorFlow should produce the real `signbridge_phrases_v1.tflite` before Android classifier integration.
