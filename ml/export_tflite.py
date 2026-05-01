#!/usr/bin/env python3
import argparse
import json
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from ml.export_metadata import build_metadata, write_metadata
from ml.signbridge_model import build_keras_model, load_phrase_map


PLACEHOLDER_HEADER = b"SIGNBRIDGE_TFLITE_PLACEHOLDER\n"


def export_tflite(
    fixture: Path,
    phrase_map: Path,
    model_output: Path,
    labels_output: Path,
    metadata_output: Path,
) -> None:
    labels = load_phrase_map(phrase_map)
    model_output.parent.mkdir(parents=True, exist_ok=True)
    labels_output.parent.mkdir(parents=True, exist_ok=True)

    try:
        tflite_bytes = export_with_tensorflow(label_count=len(labels))
        backend = "tensorflow_tflite_converter"
    except Exception as exc:
        metadata = {
            "reason": "tensorflow_unavailable",
            "detail": str(exc),
        }
        tflite_bytes = PLACEHOLDER_HEADER + json.dumps(metadata, sort_keys=True).encode("utf-8")
        backend = "placeholder_no_tensorflow"

    model_output.write_bytes(tflite_bytes)
    labels_output.write_text(json.dumps(labels, indent=2) + "\n")
    write_metadata(
        metadata_output,
        build_metadata(fixture=fixture, labels=labels, backend=backend),
    )


def export_with_tensorflow(label_count: int) -> bytes:
    import tensorflow as tf

    model = build_keras_model(label_count)
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    return converter.convert()


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--fixture", type=Path, required=True)
    parser.add_argument("--phrase-map", type=Path, required=True)
    parser.add_argument("--model-output", type=Path, required=True)
    parser.add_argument("--labels-output", type=Path, required=True)
    parser.add_argument("--metadata-output", type=Path, required=True)
    args = parser.parse_args(argv)

    export_tflite(
        fixture=args.fixture,
        phrase_map=args.phrase_map,
        model_output=args.model_output,
        labels_output=args.labels_output,
        metadata_output=args.metadata_output,
    )


if __name__ == "__main__":
    main()
