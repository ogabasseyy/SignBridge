import json
from pathlib import Path

from ml.scripts.split_dataset import split_by_take


def test_split_dataset_keeps_take_ids_out_of_both_sets():
    records = [
        {"clip_id": "a", "label": "I am Deaf", "take": 1},
        {"clip_id": "b", "label": "I am Deaf", "take": 2},
        {"clip_id": "c", "label": "Help me", "take": 1},
        {"clip_id": "d", "label": "Help me", "take": 3},
    ]

    train, validation = split_by_take(records, validation_takes={1})

    train_takes = {record["take"] for record in train}
    validation_takes = {record["take"] for record in validation}

    assert train_takes == {2, 3}
    assert validation_takes == {1}
    assert train_takes.isdisjoint(validation_takes)


def test_split_dataset_cli_writes_jsonl_files(tmp_path):
    dataset = tmp_path / "dataset.jsonl"
    train = tmp_path / "train.jsonl"
    validation = tmp_path / "validation.jsonl"
    rows = [
        {"clip_id": "a", "label": "I am Deaf", "take": 1, "frames": []},
        {"clip_id": "b", "label": "I am Deaf", "take": 2, "frames": []},
    ]
    dataset.write_text("\n".join(json.dumps(row) for row in rows) + "\n")

    from ml.scripts.split_dataset import main

    main(
        [
            "--dataset",
            str(dataset),
            "--validation-takes",
            "1",
            "--train-output",
            str(train),
            "--validation-output",
            str(validation),
        ],
    )

    assert [json.loads(line)["take"] for line in train.read_text().splitlines()] == [2]
    assert [json.loads(line)["take"] for line in validation.read_text().splitlines()] == [1]
