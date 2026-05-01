package com.signbridge.landmarks

import kotlin.math.hypot
import kotlin.math.max

object LandmarkNormalizer {
    private const val LEFT_SHOULDER = 11
    private const val RIGHT_SHOULDER = 12
    private const val LEFT_HIP = 23
    private const val RIGHT_HIP = 24
    private const val MIN_SCALE = 0.0001f

    fun normalize(frame: LandmarkFrame): FloatArray {
        val output = FloatArray(LandmarkFrame.TENSOR_SIZE)
        val torso = torsoReference(frame.pose)
        var cursor = 0

        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.pose,
            expectedCount = LandmarkFrame.POSE_COUNT,
            torso = torso,
        )
        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.leftHand,
            expectedCount = LandmarkFrame.HAND_COUNT,
            torso = torso,
        )
        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.rightHand,
            expectedCount = LandmarkFrame.HAND_COUNT,
            torso = torso,
        )
        writePoints(
            output = output,
            start = cursor,
            points = frame.face,
            expectedCount = LandmarkFrame.FACE_COUNT,
            torso = torso,
        )

        return output
    }

    private fun writePoints(
        output: FloatArray,
        start: Int,
        points: List<LandmarkPoint?>?,
        expectedCount: Int,
        torso: TorsoReference,
    ): Int {
        var cursor = start
        repeat(expectedCount) { index ->
            val point = points?.getOrNull(index)
            if (point == null) {
                cursor += LandmarkFrame.VALUES_PER_LANDMARK
            } else {
                output[cursor] = (point.x - torso.centerX) / torso.scale
                output[cursor + 1] = (point.y - torso.centerY) / torso.scale
                output[cursor + 2] = point.z / torso.scale
                cursor += LandmarkFrame.VALUES_PER_LANDMARK
            }
        }
        return cursor
    }

    private fun torsoReference(pose: List<LandmarkPoint?>): TorsoReference {
        val anchors = listOfNotNull(
            pose.getOrNull(LEFT_SHOULDER),
            pose.getOrNull(RIGHT_SHOULDER),
            pose.getOrNull(LEFT_HIP),
            pose.getOrNull(RIGHT_HIP),
        )
        if (anchors.isEmpty()) {
            return TorsoReference(centerX = 0.5f, centerY = 0.5f, scale = 1.0f)
        }

        val centerX = anchors.map { it.x }.average().toFloat()
        val centerY = anchors.map { it.y }.average().toFloat()
        val shoulderDistance = distance(
            pose.getOrNull(LEFT_SHOULDER),
            pose.getOrNull(RIGHT_SHOULDER),
        )
        val hipDistance = distance(
            pose.getOrNull(LEFT_HIP),
            pose.getOrNull(RIGHT_HIP),
        )
        val scale = max(max(shoulderDistance, hipDistance), MIN_SCALE)

        return TorsoReference(centerX = centerX, centerY = centerY, scale = scale)
    }

    private fun distance(a: LandmarkPoint?, b: LandmarkPoint?): Float {
        if (a == null || b == null) return 0.0f
        return hypot(a.x - b.x, a.y - b.y)
    }

    private data class TorsoReference(
        val centerX: Float,
        val centerY: Float,
        val scale: Float,
    )
}
