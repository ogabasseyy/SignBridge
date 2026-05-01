#!/usr/bin/env python3
import argparse
import json
import sys
from pathlib import Path


FRAME_COUNT = 30
TENSOR_SIZE = 1629
FORBIDDEN_PUBLIC_KEYS = {"camera", "audio", "person", "video_path"}
RAW_VIDEO_SUFFIXES = {".mp4", ".mov", ".mkv", ".avi", ".webm"}


def load_required_labels(phrase_map_path: Path) -> set[str]:
    data = json.loads(phrase_map_path.read_text())
    return {item["label"] for item in data["labels"]}


def load_rows(dataset_path: Path) -> list[dict]:
    return [
        json.loads(line)
        for line in dataset_path.read_text().splitlines()
        if line.strip()
    ]


def validate_dataset(dataset_path: Path, phrase_map_path: Path) -> list[str]:
    errors: list[str] = []
    if not dataset_path.exists():
        return [f"dataset not found: {dataset_path}"]

    raw_videos = [
        path
        for path in dataset_path.parent.iterdir()
        if path.is_file() and path.suffix.lower() in RAW_VIDEO_SUFFIXES
    ]
    if raw_videos:
        errors.append("raw video files are not allowed in dataset folders")

    required_labels = load_required_labels(phrase_map_path)
    rows = load_rows(dataset_path)
    labels = {row.get("label") for row in rows}
    missing = sorted(required_labels - labels)
    if missing:
        errors.append(f"missing labels: {', '.join(missing)}")

    for line_number, row in enumerate(rows, start=1):
        forbidden = sorted(FORBIDDEN_PUBLIC_KEYS.intersection(row))
        if forbidden:
            errors.append(f"line {line_number}: forbidden public keys: {', '.join(forbidden)}")

        frames = row.get("frames")
        if not isinstance(frames, list) or len(frames) != FRAME_COUNT:
            errors.append(f"line {line_number}: expected 30 frames")
            continue

        for frame_index, frame in enumerate(frames):
            if not isinstance(frame, list) or len(frame) != TENSOR_SIZE:
                errors.append(
                    f"line {line_number}, frame {frame_index}: expected tensor size {TENSOR_SIZE}",
                )
                break

    return errors


def main(argv: list[str] | None = None) -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", type=Path, required=True)
    parser.add_argument("--phrase-map", type=Path, required=True)
    args = parser.parse_args(argv)

    errors = validate_dataset(args.dataset, args.phrase_map)
    if errors:
        for error in errors:
            print(error, file=sys.stderr)
        return 1

    print("dataset valid")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
