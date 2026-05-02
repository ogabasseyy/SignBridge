import math
from typing import List, Optional, Tuple

# Constants matching the Kotlin LandmarkFrame
POSE_COUNT = 33
HAND_COUNT = 21
FACE_COUNT = 468
VALUES_PER_LANDMARK = 3
TENSOR_SIZE = (POSE_COUNT + HAND_COUNT * 2 + FACE_COUNT) * VALUES_PER_LANDMARK

# Pose landmark indices
LEFT_SHOULDER = 11
RIGHT_SHOULDER = 12
LEFT_HIP = 23
RIGHT_HIP = 24
LEFT_WRIST = 15
RIGHT_WRIST = 16

MIN_SCALE = 0.0001


class LandmarkPoint:
    def __init__(self, x: float, y: float, z: float):
        self.x = x
        self.y = y
        self.z = z


class ReferenceFrame:
    def __init__(self, center_x: float, center_y: float, scale: float):
        self.center_x = center_x
        self.center_y = center_y
        self.scale = scale


def _distance(a: Optional[LandmarkPoint], b: Optional[LandmarkPoint]) -> float:
    if a is None or b is None:
        return 0.0
    return math.hypot(a.x - b.x, a.y - b.y)


def get_wrist_reference(pose: List[Optional[LandmarkPoint]]) -> ReferenceFrame:
    """
    Computes a wrist-relative reference frame for normalization.
    Uses the midpoint between the left and right wrist as the center,
    and the torso size as the scale to normalize distances.
    """
    left_wrist = pose[LEFT_WRIST] if len(pose) > LEFT_WRIST else None
    right_wrist = pose[RIGHT_WRIST] if len(pose) > RIGHT_WRIST else None
    
    # Calculate scale using torso (shoulder to hip) to maintain size invariance
    left_shoulder = pose[LEFT_SHOULDER] if len(pose) > LEFT_SHOULDER else None
    right_shoulder = pose[RIGHT_SHOULDER] if len(pose) > RIGHT_SHOULDER else None
    left_hip = pose[LEFT_HIP] if len(pose) > LEFT_HIP else None
    right_hip = pose[RIGHT_HIP] if len(pose) > RIGHT_HIP else None
    
    shoulder_dist = _distance(left_shoulder, right_shoulder)
    hip_dist = _distance(left_hip, right_hip)
    scale = max(shoulder_dist, hip_dist)
    scale = max(scale, MIN_SCALE)

    # Calculate center using wrists if available, otherwise fallback to torso
    wrists = [w for w in (left_wrist, right_wrist) if w is not None]
    if wrists:
        center_x = sum(w.x for w in wrists) / len(wrists)
        center_y = sum(w.y for w in wrists) / len(wrists)
    else:
        torso_anchors = [p for p in (left_shoulder, right_shoulder, left_hip, right_hip) if p is not None]
        if torso_anchors:
            center_x = sum(p.x for p in torso_anchors) / len(torso_anchors)
            center_y = sum(p.y for p in torso_anchors) / len(torso_anchors)
        else:
            center_x, center_y = 0.5, 0.5

    return ReferenceFrame(center_x=center_x, center_y=center_y, scale=scale)


def _write_points(
    output: List[float],
    start: int,
    points: List[Optional[LandmarkPoint]],
    expected_count: int,
    reference: ReferenceFrame
) -> int:
    cursor = start
    for i in range(expected_count):
        point = points[i] if i < len(points) else None
        if point is None:
            # Zero-padding for missing landmarks
            output[cursor] = 0.0
            output[cursor + 1] = 0.0
            output[cursor + 2] = 0.0
            cursor += VALUES_PER_LANDMARK
        else:
            output[cursor] = (point.x - reference.center_x) / reference.scale
            output[cursor + 1] = (point.y - reference.center_y) / reference.scale
            output[cursor + 2] = point.z / reference.scale
            cursor += VALUES_PER_LANDMARK
    return cursor


def normalize_landmarks(
    pose: List[Optional[LandmarkPoint]],
    left_hand: List[Optional[LandmarkPoint]],
    right_hand: List[Optional[LandmarkPoint]],
    face: List[Optional[LandmarkPoint]]
) -> List[float]:
    """
    Normalizes a full frame of landmarks relative to the wrist-center.
    Outputs a flat array of exactly 1629 values.
    """
    output = [0.0] * TENSOR_SIZE
    reference = get_wrist_reference(pose)
    
    cursor = 0
    cursor = _write_points(output, cursor, pose, POSE_COUNT, reference)
    cursor = _write_points(output, cursor, left_hand, HAND_COUNT, reference)
    cursor = _write_points(output, cursor, right_hand, HAND_COUNT, reference)
    _write_points(output, cursor, face, FACE_COUNT, reference)
    
    return output
