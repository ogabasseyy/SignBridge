import json
from pathlib import Path


WINDOW_FRAMES = 30
LANDMARK_DIM = 1629
UNKNOWN_LABEL = "unknown"


def load_phrase_map(path: Path | None = None) -> list[dict]:
    phrase_map_path = path or Path(__file__).resolve().parent / "phrase_map.json"
    return json.loads(phrase_map_path.read_text())["labels"]


def class_count(path: Path | None = None) -> int:
    return len(load_phrase_map(path))


def build_model_config(path: Path | None = None) -> dict:
    count = class_count(path)
    return {
        "input_shape": [None, WINDOW_FRAMES, LANDMARK_DIM],
        "output_shape": [None, count],
        "window_frames": WINDOW_FRAMES,
        "landmark_dim": LANDMARK_DIM,
        "class_count": count,
        "architecture": "temporal-average-dense-baseline",
    }


def build_keras_model(label_count: int):
    import tensorflow as tf

    model = tf.keras.Sequential(
        [
            tf.keras.layers.Input(shape=(WINDOW_FRAMES, LANDMARK_DIM), name="landmarks"),
            tf.keras.layers.GlobalAveragePooling1D(name="temporal_average"),
            tf.keras.layers.Dense(128, activation="relu", name="dense"),
            tf.keras.layers.Dense(label_count, activation="softmax", name="phrase"),
        ],
        name="signbridge_phrase_classifier",
    )
    model.compile(
        optimizer="adam",
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"],
    )
    return model
