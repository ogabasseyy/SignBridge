package com.signbridge.landmarks

data class LandmarkPoint(
    val x: Float,
    val y: Float,
    val z: Float,
)

data class LandmarkFrame(
    val pose: List<LandmarkPoint?> = emptyList(),
    val leftHand: List<LandmarkPoint?>? = null,
    val rightHand: List<LandmarkPoint?>? = null,
    val face: List<LandmarkPoint?>? = null,
) {
    companion object {
        const val POSE_COUNT = 33
        const val HAND_COUNT = 21
        const val FACE_COUNT = 468
        const val TOTAL_LANDMARKS = POSE_COUNT + (HAND_COUNT * 2) + FACE_COUNT
        const val VALUES_PER_LANDMARK = 3
        const val TENSOR_SIZE = TOTAL_LANDMARKS * VALUES_PER_LANDMARK
    }
}
