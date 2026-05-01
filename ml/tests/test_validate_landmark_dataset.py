import json
import subprocess
from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]


def test_validator_accepts_complete_public_fixture(tmp_path):
    dataset = tmp_path / "synthetic_landmarks.jsonl"
    subprocess.run(
        [
            "python",
            str(REPO_ROOT / "ml/scripts/generate_synthetic_fixture.py"),
            "--output",
            str(dataset),
        ],
        cwd=REPO_ROOT,
        check=True,
    )

    result = run_validator(dataset)

    assert result.returncode == 0, result.stderr


def test_validator_rejects_missing_phrase(tmp_path):
    dataset = tmp_path / "missing_phrase.jsonl"
    rows = complete_rows()
    rows = [row for row in rows if row["label"] != "Use my phone"]
    write_rows(dataset, rows)

    result = run_validator(dataset)

    assert result.returncode != 0
    assert "missing labels" in result.stderr.lower()


def test_validator_rejects_wrong_frame_count(tmp_path):
    dataset = tmp_path / "wrong_frame_count.jsonl"
    rows = complete_rows()
    rows[0]["frames"] = rows[0]["frames"][:29]
    write_rows(dataset, rows)

    result = run_validator(dataset)

    assert result.returncode != 0
    assert "30 frames" in result.stderr.lower()


def test_validator_rejects_raw_video_files(tmp_path):
    dataset = tmp_path / "dataset.jsonl"
    write_rows(dataset, complete_rows())
    (tmp_path / "capture.mp4").write_bytes(b"not public")

    result = run_validator(dataset)

    assert result.returncode != 0
    assert "raw video" in result.stderr.lower()


def complete_rows():
    fixture = REPO_ROOT / "ml/fixtures/synthetic_landmarks.jsonl"
    return [json.loads(line) for line in fixture.read_text().splitlines()]


def write_rows(path, rows):
    path.write_text("\n".join(json.dumps(row) for row in rows) + "\n")


def run_validator(dataset):
    return subprocess.run(
        [
            "python",
            str(REPO_ROOT / "ml/scripts/validate_landmark_dataset.py"),
            "--dataset",
            str(dataset),
            "--phrase-map",
            str(REPO_ROOT / "ml/phrase_map.json"),
        ],
        cwd=REPO_ROOT,
        text=True,
        capture_output=True,
    )
