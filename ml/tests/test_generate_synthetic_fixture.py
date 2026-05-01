import json
import subprocess
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]


def test_generate_synthetic_fixture_has_all_labels_and_public_shape(tmp_path):
    output = tmp_path / "synthetic_landmarks.jsonl"

    subprocess.run(
        [
            "python",
            str(REPO_ROOT / "ml/scripts/generate_synthetic_fixture.py"),
            "--output",
            str(output),
        ],
        cwd=REPO_ROOT,
        check=True,
    )

    rows = [json.loads(line) for line in output.read_text().splitlines()]
    labels = {row["label"] for row in rows}

    assert len(rows) == 26
    assert len(labels) == 26
    assert "unknown" in labels

    for row in rows:
        assert len(row["frames"]) == 30
        assert all(len(frame) == 1629 for frame in row["frames"])
        assert "camera" not in row
        assert "audio" not in row
        assert "person" not in row
        assert "video_path" not in row
