def top_k_accuracy(
    logits: list[list[float]],
    labels: list[int],
    k: int,
) -> float:
    if not logits:
        return 0.0

    hits = 0
    for row, label in zip(logits, labels, strict=True):
        ranked = sorted(range(len(row)), key=lambda index: row[index], reverse=True)
        if label in ranked[:k]:
            hits += 1
    return hits / len(logits)


def argmax(row: list[float]) -> int:
    return max(range(len(row)), key=lambda index: row[index])


def confusion_matrix(
    logits: list[list[float]],
    labels: list[int],
    class_count: int,
) -> list[list[int]]:
    matrix = [[0 for _ in range(class_count)] for _ in range(class_count)]
    for row, label in zip(logits, labels, strict=True):
        prediction = argmax(row)
        matrix[label][prediction] += 1
    return matrix
