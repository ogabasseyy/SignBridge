# Model Card: SignBridge 1D-CNN + Transformer

## Model Details
* **Architecture:** 1D-CNN Stem + Transformer Encoder (Self-Attention) + ECA (Efficient Channel Attention) Blocks.
* **Input:** `[1, 60, 1629]` dimensional tensor representing 60 temporal frames of MediaPipe Holistic landmarks (flattened).
* **Output:** Softmax probabilities across 26 classes (25 specific Nigerian Sign Language phrases + 1 `UNKNOWN` class).
* **Framework:** TensorFlow Lite (`.tflite`) converted from Keras.
* **Size:** ~850 KB.
* **Release Date:** May 2026.

## Intended Use
* **Primary Use Case:** Real-time on-device translation of specific Nigerian Sign Language (NSL) emergency and functional phrases into text.
* **Target Audience:** Deaf and Hard of Hearing (DHH) individuals communicating with non-signers in high-stakes scenarios (e.g., medical, roadside, retail).
* **Out-of-Scope:** This model is **NOT** a general-purpose conversational sign language translator. It is strictly limited to the 25 programmed phrases. It is not intended to replace professional human interpreters in legal or complex medical environments.

## Training Data
* **Warm-start:** Pre-trained on the ASL-Citizen dataset to learn robust spatio-temporal feature extraction for hand and pose dynamics.
* **Fine-tuning:** Fine-tuned on the **Lanfrica NSL Dataset** specifically for the 25 selected Nigerian Sign Language phrases.
* **Data Processing:** All video frames were passed through MediaPipe Tasks Vision to extract 3D landmarks. 
* **Normalization:** Landmarks are spatially normalized (root-centered and wrist-relative) prior to training to ensure invariance to the signer's distance from the camera and physical proportions.

## Evaluation Data & Metrics
* **Slice-Based Testing:** Evaluated separately across different lighting conditions, skin tones (Fitzpatrick scale), and camera angles to ensure parity.
* **Thresholds:** The model utilizes an exponential smoothing buffer and requires a high confidence threshold (>0.85) to trigger a translation, favoring "false negatives" (asking the user to repeat) over "false positives" (translating the wrong phrase).

## Ethical Considerations & Fairness
* **Adversarial Defenses:** The application isolates the sign classifier from the Generative AI text-condensation step. The LLM acts purely as a linguistic condenser of pre-approved, safe classifications.
* **Privacy:** The model runs 100% on-device. No raw images, videos, or audio are ever uploaded to a server. Only the extracted numerical landmark coordinates are fed to the model.

## Caveats and Recommendations
* **Frame Rate Dependency:** The model is highly dependent on a stable 30fps camera feed. Severe frame drops on older devices may result in temporal distortion, pushing signs into the `UNKNOWN` classification.
* **Environmental Limits:** Extreme backlighting or deep shadows that obscure hand visibility will cause MediaPipe extraction to fail, preventing the model from running.
