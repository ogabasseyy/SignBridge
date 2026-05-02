#!/usr/bin/env bash
set -e

echo "============================================================"
echo " SignBridge: Pre-training (ASL) & Fine-Tuning (NSL) Script "
echo "============================================================"

# This script documents the 2026 Transfer Learning strategy for SignBridge.
# It assumes you have downloaded the 46GB ASL-Citizen dataset to a cloud 
# machine with a GPU, extracted the landmarks, and have the Lanfrica NSL 
# landmarks ready.

# 1. Train the Base Representation Model on American Sign Language
echo "[1/3] Training Base Representation Model on 2,731 ASL classes..."
python ml/train_classifier.py \
    --dataset ml/private/asl_citizen/asl_landmarks.jsonl \
    --phrase-map ml/private/asl_citizen/asl_phrase_map.json \
    --output-dir ml/out/asl_base_model \
    --epochs 100 \
    --batch-size 128

# 2. Fine-tune on Nigerian Sign Language
# To fine-tune, you would load the Keras weights from asl_base_model,
# freeze the Transformer/ECA layers, replace the final classification head
# with the 137 NSL classes, and train with a lower learning rate.
echo "[2/3] Fine-tuning Head on Lanfrica NSL Dataset..."
python ml/train_classifier.py \
    --dataset ml/private/lanfrica/nsl_landmarks.jsonl \
    --phrase-map ml/phrase_map.json \
    --output-dir ml/out/nsl_finetuned_model \
    --epochs 50 \
    --batch-size 32 \
    --warm-start ml/out/asl_base_model/best_model.keras

# 3. Export to TensorFlow Lite
echo "[3/3] Exporting to TensorFlow Lite for On-Device Inference..."
python ml/scripts/export_tflite.py \
    --keras-model ml/out/nsl_finetuned_model/best_model.keras \
    --output app/src/main/assets/signbridge_model.tflite

echo "✅ Transfer Learning Pipeline Complete! Model is ready for Android."
