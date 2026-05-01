#!/usr/bin/env python3
import argparse
import json
from pathlib import Path
from typing import Iterable


def split_by_take(
    records: Iterable[dict],
    validation_takes: set[int],
) -> tuple[list[dict], list[dict]]:
    train: list[dict] = []
    validation: list[dict] = []

    for record in records:
        take = int(record["take"])
        if take in validation_takes:
            validation.append(record)
        else:
            train.append(record)

    return train, validation


def read_jsonl(path: Path) -> list[dict]:
    return [
        json.loads(line)
        for line in path.read_text().splitlines()
        if line.strip()
    ]


def write_jsonl(path: Path, rows: list[dict]) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    if rows:
        path.write_text("\n".join(json.dumps(row, separators=(",", ":")) for row in rows) + "\n")
    else:
        path.write_text("")


def parse_takes(value: str) -> set[int]:
    return {int(item.strip()) for item in value.split(",") if item.strip()}


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", type=Path, required=True)
    parser.add_argument("--validation-takes", required=True)
    parser.add_argument("--train-output", type=Path, required=True)
    parser.add_argument("--validation-output", type=Path, required=True)
    args = parser.parse_args(argv)

    train, validation = split_by_take(
        records=read_jsonl(args.dataset),
        validation_takes=parse_takes(args.validation_takes),
    )
    write_jsonl(args.train_output, train)
    write_jsonl(args.validation_output, validation)


if __name__ == "__main__":
    main()
