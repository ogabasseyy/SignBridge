import hashlib
import json
from pathlib import Path

from ml.signbridge_model import LANDMARK_DIM, WINDOW_FRAMES


def dataset_hash(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def build_metadata(
    fixture: Path,
    labels: list[dict],
    backend: str,
) -> dict:
    return {
        "model_version": "signbridge_phrases_v1",
        "normalization_version": "torso_center_scale_v1",
        "backend": backend,
        "input_shape": [1, WINDOW_FRAMES, LANDMARK_DIM],
        "output_shape": [1, len(labels)],
        "input_dtype": "float32",
        "output_dtype": "float32",
        "label_count": len(labels),
        "dataset_sha256": dataset_hash(fixture),
    }


def write_metadata(path: Path, metadata: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(metadata, indent=2, sort_keys=True) + "\n")
