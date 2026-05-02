import tensorflow as tf
import pytest
from ml.augmentations import time_warp, finger_dropout, spatial_noise, POSE_END, LEFT_HAND_END

def test_time_warp_preserves_shape():
    sequence = tf.random.normal([30, 1629])
    warped = time_warp(sequence)
    assert warped.shape == (30, 1629)

def test_finger_dropout_drops_correct_indices():
    # Force dropout by passing 1.0 prob
    sequence = tf.ones([30, 1629])
    
    # We can't guarantee both drop because they are independent random rolls, 
    # but we can check if it sets exactly 63 values to zero if a drop happens.
    # To test deterministically, let's test the independent logic or just 
    # check that the output shape is preserved.
    dropped = finger_dropout(sequence, dropout_prob=1.0)
    assert dropped.shape == (30, 1629)
    
    # Since prob is 1.0, both left and right hand should be zeroed
    # Left hand: 99 to 99+63=162
    # Right hand: 162 to 162+63=225
    left_hand = dropped[:, POSE_END:LEFT_HAND_END]
    right_hand = dropped[:, LEFT_HAND_END:LEFT_HAND_END+63]
    
    # All values in hand sections should be 0.0
    tf.debugging.assert_near(tf.reduce_sum(left_hand), 0.0)
    tf.debugging.assert_near(tf.reduce_sum(right_hand), 0.0)

def test_spatial_noise_preserves_zeros():
    # Sequence with exactly zero values
    sequence = tf.zeros([30, 1629])
    noised = spatial_noise(sequence, noise_stddev=1.0)
    
    # Zeroes should remain zeroes
    tf.debugging.assert_near(tf.reduce_sum(noised), 0.0)
    
    # Sequence with non-zero values
    sequence = tf.ones([30, 1629])
    noised = spatial_noise(sequence, noise_stddev=1.0)
    
    # Non-zeroes should change
    assert not tf.reduce_all(tf.math.equal(noised, sequence))
