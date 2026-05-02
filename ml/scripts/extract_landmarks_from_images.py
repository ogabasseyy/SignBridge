#!/usr/bin/env python3
import argparse
import json
import logging
import sys
from pathlib import Path
from typing import Optional

import cv2
import numpy as np

REPO_ROOT = Path(__file__).resolve().parents[2]
if str(REPO_ROOT) not in sys.path:
    sys.path.insert(0, str(REPO_ROOT))

from ml.landmarks.normalization import LandmarkPoint, normalize_landmarks

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")


def _to_points(landmark_list) -> list[Optional[LandmarkPoint]]:
    if not landmark_list:
        return []
    return [
        LandmarkPoint(x=lm.x, y=lm.y, z=lm.z) if lm.visibility >= 0.0 else None
        for lm in landmark_list.landmark
    ]


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dataset", type=str, required=True, help="Hugging Face dataset name")
    parser.add_argument("--split", type=str, default="train", help="Dataset split to process")
    parser.add_argument("--output", type=Path, required=True, help="Path to output JSONL file")
    parser.add_argument("--window-frames", type=int, default=30, help="Number of frames per sequence")
    args = parser.parse_args(argv)

    try:
        from datasets import load_dataset
    except ImportError:
        logging.error("The 'datasets' package is required. Run: pip install datasets")
        sys.exit(1)

    import mediapipe as mp
    mp_holistic = mp.solutions.holistic

    logging.info(f"Loading dataset: {args.dataset}")
    try:
        ds = load_dataset(args.dataset, split=args.split, streaming=True)
    except Exception as e:
        logging.error(f"Failed to load dataset (ensure you have HF_TOKEN set if gated): {e}")
        sys.exit(1)

    args.output.parent.mkdir(parents=True, exist_ok=True)
    out_file = args.output.open("w", encoding="utf-8")

    processed = 0
    with mp_holistic.Holistic(
        static_image_mode=True,
        model_complexity=1,
        enable_segmentation=False,
        refine_face_landmarks=True
    ) as holistic:
        
        # We simulate a sequence of frames by duplicating the static image.
        # In a real video dataset, we would process multiple frames.
        # For this image dataset (Lanfrica MVP), we tile it to match WINDOW_FRAMES.
        
        for idx, sample in enumerate(ds):
            if 'image' not in sample or 'label' not in sample:
                logging.warning(f"Sample {idx} missing 'image' or 'label'. Skipping.")
                continue

            # Convert PIL image to OpenCV format (RGB)
            pil_img = sample['image']
            image_np = np.array(pil_img.convert('RGB'))
            
            results = holistic.process(image_np)
            
            # Convert MediaPipe landmarks to our generic LandmarkPoint format
            pose = _to_points(results.pose_landmarks)
            left_hand = _to_points(results.left_hand_landmarks)
            right_hand = _to_points(results.right_hand_landmarks)
            face = _to_points(results.face_landmarks)
            
            # Root-Landmark Normalization (Wrist-Relative)
            normalized_frame = normalize_landmarks(pose, left_hand, right_hand, face)
            
            # Since these are static images, duplicate to fill the temporal window
            sequence = [normalized_frame for _ in range(args.window_frames)]
            
            label_value = sample['label']
            if isinstance(label_value, int) and 'gloss' in sample:
                # If label is an int index, try to get the string representation
                label_value = sample.get('gloss', str(label_value))
                
            record = {
                "label": str(label_value),
                "frames": sequence
            }
            out_file.write(json.dumps(record) + "\n")
            processed += 1
            
            if processed % 100 == 0:
                logging.info(f"Processed {processed} images...")

    out_file.close()
    logging.info(f"Finished. Extracted {processed} sequences to {args.output}")


if __name__ == "__main__":
    main()
