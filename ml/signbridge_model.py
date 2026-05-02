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
        "architecture": "transformer-encoder-v1",
    }


def build_keras_model(label_count: int):
    import tensorflow as tf
    from tensorflow.keras import layers

    inputs = layers.Input(shape=(WINDOW_FRAMES, LANDMARK_DIM), name="landmarks")
    
    # 1D CNN Stem for local temporal smoothing
    x = layers.Conv1D(filters=128, kernel_size=3, padding="same", activation="relu", name="cnn_stem")(inputs)
    x = layers.LayerNormalization(epsilon=1e-6)(x)
    
    # Positional Encoding (learned)
    positions = tf.range(start=0, limit=WINDOW_FRAMES, delta=1)
    pos_embedding = layers.Embedding(input_dim=WINDOW_FRAMES, output_dim=128, name="pos_embedding")(positions)
    x = x + pos_embedding

    # Transformer Blocks
    for i in range(2):
        # Self-Attention
        attention_output = layers.MultiHeadAttention(num_heads=4, key_dim=128, dropout=0.1, name=f"mha_{i}")(x, x)
        x = layers.Add()([x, attention_output])
        x = layers.LayerNormalization(epsilon=1e-6)(x)
        
        # Feed Forward
        ffn = layers.Dense(128, activation="relu", name=f"ffn_{i}_1")(x)
        ffn = layers.Dense(128, name=f"ffn_{i}_2")(ffn)
        x = layers.Add()([x, ffn])
        x = layers.LayerNormalization(epsilon=1e-6)(x)

    # ECA (Efficient Channel Attention) Block
    channels = x.shape[-1]
    squeeze = layers.GlobalAveragePooling1D(name="eca_squeeze")(x)
    squeeze_reshaped = layers.Reshape((channels, 1), name="eca_reshape_in")(squeeze)
    eca_conv = layers.Conv1D(1, kernel_size=3, padding="same", use_bias=False, name="eca_conv")(squeeze_reshaped)
    eca_act = layers.Activation("sigmoid", name="eca_sigmoid")(eca_conv)
    eca_weights = layers.Reshape((1, channels), name="eca_reshape_out")(eca_act)
    x = layers.Multiply(name="eca_multiply")([x, eca_weights])

    # Global Pooling and Classification
    x = layers.GlobalAveragePooling1D(name="temporal_pool")(x)
    x = layers.Dropout(0.2)(x)
    outputs = layers.Dense(label_count, activation="softmax", name="phrase")(x)

    model = tf.keras.Model(inputs=inputs, outputs=outputs, name="signbridge_transformer_eca")
    model.compile(
        optimizer="adam",
        loss="sparse_categorical_crossentropy",
        metrics=["accuracy"],
    )
    return model
