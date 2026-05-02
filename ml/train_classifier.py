#!/usr/bin/env python3
import argparse
import json
import logging
import sys
from pathlib import Path

import tensorflow as tf
import numpy as np

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from ml.signbridge_model import build_model_config, load_phrase_map, build_keras_model
from ml.augmentations import augment_sequence

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

def _parse_jsonl(file_path: Path, label_to_id: dict) -> tuple[np.ndarray, np.ndarray]:
    """Loads all sequences and labels into memory for tf.data."""
    sequences = []
    labels = []
    
    with open(file_path, "r", encoding="utf-8") as f:
        for line in f:
            if not line.strip():
                continue
            record = json.loads(line)
            label_str = record["label"]
            if label_str not in label_to_id:
                logging.warning(f"Unknown label '{label_str}' in dataset, skipping.")
                continue
            
            # Sequence shape: (WINDOW_FRAMES, LANDMARK_DIM)
            seq = np.array(record["frames"], dtype=np.float32)
            sequences.append(seq)
            labels.append(label_to_id[label_str])
            
    return np.array(sequences), np.array(labels)

def create_dataset(features: np.ndarray, labels: np.ndarray, batch_size: int, is_training: bool = True) -> tf.data.Dataset:
    ds = tf.data.Dataset.from_tensor_slices((features, labels))
    
    if is_training:
        ds = ds.shuffle(buffer_size=1000)
        ds = ds.map(augment_sequence, num_parallel_calls=tf.data.AUTOTUNE)
        
    ds = ds.batch(batch_size).prefetch(tf.data.AUTOTUNE)
    return ds

def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", type=Path, required=True, help="Path to JSONL dataset")
    parser.add_argument("--phrase-map", type=Path, required=True, help="Path to phrase_map.json")
    parser.add_argument("--output-dir", type=Path, required=True, help="Directory to save model")
    parser.add_argument("--epochs", type=int, default=50, help="Training epochs")
    parser.add_argument("--batch-size", type=int, default=32, help="Batch size")
    args = parser.parse_args(argv)

    args.output_dir.mkdir(parents=True, exist_ok=True)
    phrase_list = load_phrase_map(args.phrase_map)
    label_to_id = {item["label"]: idx for idx, item in enumerate(phrase_list)}

    logging.info(f"Loading dataset from {args.dataset}")
    features, labels = _parse_jsonl(args.dataset, label_to_id)
    logging.info(f"Loaded {len(features)} sequences across {len(set(labels))} classes")

    if len(features) == 0:
        logging.error("No valid data found to train.")
        sys.exit(1)

    # 80/20 train/val split (deterministic for simplicity)
    np.random.seed(42)
    indices = np.random.permutation(len(features))
    split = int(0.8 * len(features))
    
    train_idx, val_idx = indices[:split], indices[split:]
    train_ds = create_dataset(features[train_idx], labels[train_idx], args.batch_size, is_training=True)
    val_ds = create_dataset(features[val_idx], labels[val_idx], args.batch_size, is_training=False)

    logging.info("Building model architecture")
    model = build_keras_model(len(phrase_list))
    
    callbacks = [
        tf.keras.callbacks.EarlyStopping(monitor="val_loss", patience=10, restore_best_weights=True),
        tf.keras.callbacks.ModelCheckpoint(str(args.output_dir / "best_model.keras"), save_best_only=True)
    ]

    logging.info("Starting training loop")
    history = model.fit(
        train_ds,
        validation_data=val_ds,
        epochs=args.epochs,
        callbacks=callbacks
    )

    logging.info(f"Training complete. Saving final model to {args.output_dir}")
    model.save(args.output_dir / "final_model.keras")
    
    # Save training summary
    summary = {
        "status": "trained",
        "epochs_run": len(history.history["loss"]),
        "final_loss": history.history["loss"][-1],
        "final_val_loss": history.history["val_loss"][-1],
        "final_accuracy": history.history["accuracy"][-1],
        "final_val_accuracy": history.history["val_accuracy"][-1],
        "model_config": build_model_config(args.phrase_map),
        "phrase_count": len(phrase_list),
        "dataset_size": len(features)
    }
    
    (args.output_dir / "training_summary.json").write_text(
        json.dumps(summary, indent=2, sort_keys=True) + "\n"
    )

if __name__ == "__main__":
    main()
