from ml.evaluate_classifier import confusion_matrix, top_k_accuracy


def test_top_k_accuracy_from_fixed_logits():
    logits = [
        [0.9, 0.1, 0.0],
        [0.1, 0.2, 0.7],
        [0.4, 0.5, 0.1],
    ]
    labels = [0, 1, 2]

    assert top_k_accuracy(logits, labels, k=1) == 1 / 3
    assert top_k_accuracy(logits, labels, k=2) == 2 / 3
    assert top_k_accuracy(logits, labels, k=3) == 1.0


def test_confusion_matrix_counts_predictions():
    logits = [
        [0.9, 0.1, 0.0],
        [0.1, 0.2, 0.7],
        [0.4, 0.5, 0.1],
    ]
    labels = [0, 1, 2]

    assert confusion_matrix(logits, labels, class_count=3) == [
        [1, 0, 0],
        [0, 0, 1],
        [0, 1, 0],
    ]
