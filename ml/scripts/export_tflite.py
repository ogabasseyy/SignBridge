#!/usr/bin/env python3
import argparse
import logging
from pathlib import Path

import tensorflow as tf

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser(description="Export a Keras model to TensorFlow Lite for Android inference.")
    parser.add_argument("--keras-model", type=Path, required=True, help="Path to input .keras model")
    parser.add_argument("--output", type=Path, required=True, help="Path to output .tflite file")
    parser.add_argument("--optimize", action="store_true", help="Apply TFLite default optimizations (quantization)")
    args = parser.parse_args(argv)

    if not args.keras_model.exists():
        logging.error(f"Input model not found: {args.keras_model}")
        return

    logging.info(f"Loading Keras model from {args.keras_model}")
    model = tf.keras.models.load_model(args.keras_model)

    logging.info("Converting to TFLite...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    
    # Optional: Apply post-training quantization to make the model smaller & faster
    if args.optimize:
        logging.info("Applying default optimizations (8-bit quantization)")
        converter.optimizations = [tf.lite.Optimize.DEFAULT]
        
    # Transformer Ops require Select TF Ops or specific TFLite builtin ops
    # The MultiHeadAttention might require Select TF ops if standard doesn't support it
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS, 
        tf.lite.OpsSet.SELECT_TF_OPS 
    ]
    
    tflite_model = converter.convert()

    args.output.parent.mkdir(parents=True, exist_ok=True)
    with open(args.output, "wb") as f:
        f.write(tflite_model)
        
    logging.info(f"Successfully exported TFLite model to {args.output}")
    logging.info(f"Model size: {len(tflite_model) / 1024 / 1024:.2f} MB")

if __name__ == "__main__":
    main()
