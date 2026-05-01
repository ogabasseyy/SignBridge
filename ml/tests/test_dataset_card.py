from pathlib import Path


REPO_ROOT = Path(__file__).resolve().parents[2]


def test_dataset_card_documents_consent_privacy_and_public_fixture():
    card = REPO_ROOT / "ml/DATASET_CARD.md"

    text = card.read_text().lower()

    required_sections = [
        "consent",
        "privacy",
        "private captures",
        "public synthetic fixture",
        "phrase labels",
        "reviewer reproduction commands",
    ]

    for section in required_sections:
        assert section in text
