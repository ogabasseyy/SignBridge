#!/usr/bin/env python3
import argparse
import json
import sys
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from ml.signbridge_model import build_model_config, load_phrase_map


def summarize_dataset(dataset: Path) -> dict:
    rows = [
        json.loads(line)
        for line in dataset.read_text().splitlines()
        if line.strip()
    ]
    labels = sorted({row["label"] for row in rows})
    return {
        "sample_count": len(rows),
        "labels_seen": labels,
    }


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", type=Path, required=True)
    parser.add_argument("--phrase-map", type=Path, required=True)
    parser.add_argument("--output-dir", type=Path, required=True)
    args = parser.parse_args(argv)

    args.output_dir.mkdir(parents=True, exist_ok=True)
    labels = load_phrase_map(args.phrase_map)
    summary = {
        "status": "contract_smoke_only",
        "note": "Install TensorFlow in the training environment for real classifier fitting.",
        "model_config": build_model_config(args.phrase_map),
        "phrase_count": len(labels),
        "dataset": summarize_dataset(args.dataset),
    }
    (args.output_dir / "training_summary.json").write_text(
        json.dumps(summary, indent=2, sort_keys=True) + "\n",
    )


if __name__ == "__main__":
    main()
