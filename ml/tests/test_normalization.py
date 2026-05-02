import pytest
import math
from ml.landmarks.normalization import LandmarkPoint, get_wrist_reference, normalize_landmarks, POSE_COUNT, HAND_COUNT, FACE_COUNT

def test_get_wrist_reference_computes_scale_correctly():
    # Pose with only shoulders
    pose = [None] * POSE_COUNT
    pose[11] = LandmarkPoint(0.0, 0.0, 0.0) # Left shoulder
    pose[12] = LandmarkPoint(2.0, 0.0, 0.0) # Right shoulder
    
    ref = get_wrist_reference(pose)
    # distance = 2.0
    assert ref.scale == 2.0
    # Torso anchors exist, no wrists, so center is average of shoulders
    assert ref.center_x == 1.0
    assert ref.center_y == 0.0

def test_get_wrist_reference_uses_wrists_for_center():
    pose = [None] * POSE_COUNT
    pose[11] = LandmarkPoint(0.0, 0.0, 0.0) # Left shoulder
    pose[12] = LandmarkPoint(10.0, 0.0, 0.0) # Right shoulder
    pose[15] = LandmarkPoint(4.0, 5.0, 0.0) # Left wrist
    pose[16] = LandmarkPoint(6.0, 5.0, 0.0) # Right wrist
    
    ref = get_wrist_reference(pose)
    assert ref.scale == 10.0
    # Center should be exactly halfway between the wrists
    assert ref.center_x == 5.0
    assert ref.center_y == 5.0

def test_normalize_landmarks_produces_correct_tensor_size():
    pose = [None] * POSE_COUNT
    left = [None] * HAND_COUNT
    right = [None] * HAND_COUNT
    face = [None] * FACE_COUNT
    
    output = normalize_landmarks(pose, left, right, face)
    assert len(output) == 1629

def test_normalize_landmarks_shifts_and_scales():
    pose = [None] * POSE_COUNT
    # Shoulders at distance 2.0
    pose[11] = LandmarkPoint(0.0, 0.0, 0.0)
    pose[12] = LandmarkPoint(2.0, 0.0, 0.0)
    # Wrists at (1.0, 1.0)
    pose[15] = LandmarkPoint(1.0, 1.0, 0.0)
    
    left = [None] * HAND_COUNT
    # Hand point exactly at the wrist center (1.0, 1.0) should become (0,0)
    left[0] = LandmarkPoint(1.0, 1.0, 0.0)
    # Hand point offset by +1.0 in X, should become +0.5 after scaling by 2.0
    left[1] = LandmarkPoint(2.0, 1.0, 0.0)
    
    output = normalize_landmarks(pose, left, [], [])
    
    # Check left hand [0] coordinates (x, y, z)
    # The left hand array starts after pose (33 * 3 = 99)
    # output[99] is left[0].x, output[100] is left[0].y
    assert math.isclose(output[99], 0.0, abs_tol=1e-5)
    assert math.isclose(output[100], 0.0, abs_tol=1e-5)
    
    # Check left hand [1] coordinates
    assert math.isclose(output[102], 0.5, abs_tol=1e-5)
    assert math.isclose(output[103], 0.0, abs_tol=1e-5)
