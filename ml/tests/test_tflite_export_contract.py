import json
import subprocess
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]


def test_tflite_export_contract_matches_phrase_map(tmp_path):
    model_output = tmp_path / "signbridge_phrases_v1.tflite"
    labels_output = tmp_path / "signbridge_phrases_v1.labels.json"
    metadata_output = tmp_path / "signbridge_phrases_v1.metadata.json"

    subprocess.run(
        [
            "python",
            str(REPO_ROOT / "ml/export_tflite.py"),
            "--fixture",
            str(REPO_ROOT / "ml/fixtures/synthetic_landmarks.jsonl"),
            "--phrase-map",
            str(REPO_ROOT / "ml/phrase_map.json"),
            "--model-output",
            str(model_output),
            "--labels-output",
            str(labels_output),
            "--metadata-output",
            str(metadata_output),
        ],
        cwd=REPO_ROOT,
        check=True,
    )

    phrase_map = json.loads((REPO_ROOT / "ml/phrase_map.json").read_text())
    labels = json.loads(labels_output.read_text())
    metadata = json.loads(metadata_output.read_text())

    assert model_output.exists()
    assert labels == phrase_map["labels"]
    assert metadata["input_shape"] == [1, 30, 1629]
    assert metadata["output_shape"] == [1, 26]
    assert metadata["input_dtype"] == "float32"
    assert metadata["output_dtype"] == "float32"
    assert metadata["label_count"] == 26
