#!/usr/bin/env python3
import argparse
import json
from pathlib import Path


FRAME_COUNT = 30
TENSOR_SIZE = 1629
FORBIDDEN_PUBLIC_KEYS = {"camera", "audio", "person", "video_path"}


def load_labels(phrase_map_path: Path) -> list[str]:
    data = json.loads(phrase_map_path.read_text())
    return [item["label"] for item in data["labels"]]


def synthetic_frame(label_index: int, frame_index: int) -> list[float]:
    frame = [0.0] * TENSOR_SIZE
    base = (label_index + 1) * 0.001
    frame[0] = round(base, 6)
    frame[1] = round(frame_index / FRAME_COUNT, 6)
    frame[2] = round((label_index + frame_index) * 0.0001, 6)
    return frame


def generate_rows(labels: list[str]) -> list[dict]:
    rows = []
    for label_index, label in enumerate(labels):
        row = {
            "clip_id": f"synthetic-{label_index + 1:02d}",
            "label": label,
            "take": 0,
            "frames": [
                synthetic_frame(label_index, frame_index)
                for frame_index in range(FRAME_COUNT)
            ],
        }
        assert FORBIDDEN_PUBLIC_KEYS.isdisjoint(row)
        rows.append(row)
    return rows


def write_jsonl(path: Path, rows: list[dict]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(json.dumps(row, separators=(",", ":")) for row in rows) + "\n")


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", type=Path, required=True)
    parser.add_argument(
        "--phrase-map",
        type=Path,
        default=Path(__file__).resolve().parents[1] / "phrase_map.json",
    )
    args = parser.parse_args(argv)

    labels = load_labels(args.phrase_map)
    write_jsonl(args.output, generate_rows(labels))


if __name__ == "__main__":
    main()
