import tensorflow as tf

# Data layout indices
POSE_END = 99
LEFT_HAND_END = POSE_END + 63
RIGHT_HAND_END = LEFT_HAND_END + 63

@tf.function
def time_warp(sequence: tf.Tensor) -> tf.Tensor:
    """
    Randomly crops the sequence temporally and resizes it back to original length.
    Simulates signing at different speeds.
    """
    frames = tf.shape(sequence)[0]
    features = tf.shape(sequence)[1]
    
    # Random crop size between 70% and 100% of original frames
    crop_size = tf.random.uniform([], minval=tf.cast(tf.cast(frames, tf.float32) * 0.7, tf.int32), maxval=frames, dtype=tf.int32)
    start_idx = tf.random.uniform([], minval=0, maxval=frames - crop_size + 1, dtype=tf.int32)
    
    cropped = sequence[start_idx : start_idx + crop_size, :]
    
    # Reshape for tf.image.resize
    cropped_expanded = tf.reshape(cropped, [crop_size, features, 1])
    
    # Resize back to original number of frames using bilinear interpolation
    resized = tf.image.resize(cropped_expanded, [frames, features])
    
    return tf.reshape(resized, [frames, features])

@tf.function
def finger_dropout(sequence: tf.Tensor, dropout_prob: float = 0.2) -> tf.Tensor:
    """
    Randomly drops (sets to zero) the left hand or right hand features to simulate occlusion.
    """
    # Create masks that are 1.0 everywhere
    mask = tf.ones_like(sequence)
    
    # Decide to drop left hand
    if tf.random.uniform([]) < dropout_prob:
        left_hand_mask = tf.concat([
            tf.ones([tf.shape(sequence)[0], POSE_END]),
            tf.zeros([tf.shape(sequence)[0], 63]),
            tf.ones([tf.shape(sequence)[0], tf.shape(sequence)[1] - LEFT_HAND_END])
        ], axis=1)
        mask = mask * left_hand_mask
        
    # Decide to drop right hand
    if tf.random.uniform([]) < dropout_prob:
        right_hand_mask = tf.concat([
            tf.ones([tf.shape(sequence)[0], LEFT_HAND_END]),
            tf.zeros([tf.shape(sequence)[0], 63]),
            tf.ones([tf.shape(sequence)[0], tf.shape(sequence)[1] - RIGHT_HAND_END])
        ], axis=1)
        mask = mask * right_hand_mask
        
    return sequence * mask

@tf.function
def spatial_noise(sequence: tf.Tensor, noise_stddev: float = 0.01) -> tf.Tensor:
    """
    Adds tiny Gaussian noise to the coordinates to prevent overfitting to exact positions.
    Zero values (padding/missing landmarks) remain zero.
    """
    noise = tf.random.normal(shape=tf.shape(sequence), mean=0.0, stddev=noise_stddev)
    
    # Ensure missing landmarks (which are exactly 0.0) don't get noise
    non_zero_mask = tf.cast(tf.abs(sequence) > 1e-6, tf.float32)
    
    return sequence + (noise * non_zero_mask)

@tf.function
def augment_sequence(sequence: tf.Tensor, label: tf.Tensor) -> tuple[tf.Tensor, tf.Tensor]:
    """
    Applies the full augmentation pipeline to a single sequence.
    Intended for use in tf.data.Dataset.map()
    """
    # 50% chance to apply time warping
    sequence = tf.cond(tf.random.uniform([]) < 0.5, lambda: time_warp(sequence), lambda: sequence)
    
    # Apply finger dropout
    sequence = finger_dropout(sequence)
    
    # Apply spatial noise
    sequence = spatial_noise(sequence)
    
    return sequence, label
