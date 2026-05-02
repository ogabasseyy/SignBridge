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
        val reference = referenceFrame(frame.pose)
        var cursor = 0

        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.pose,
            expectedCount = LandmarkFrame.POSE_COUNT,
            reference = reference,
        )
        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.leftHand,
            expectedCount = LandmarkFrame.HAND_COUNT,
            reference = reference,
        )
        cursor = writePoints(
            output = output,
            start = cursor,
            points = frame.rightHand,
            expectedCount = LandmarkFrame.HAND_COUNT,
            reference = reference,
        )
        writePoints(
            output = output,
            start = cursor,
            points = frame.face,
            expectedCount = LandmarkFrame.FACE_COUNT,
            reference = reference,
        )

        return output
    }

    private fun writePoints(
        output: FloatArray,
        start: Int,
        points: List<LandmarkPoint?>?,
        expectedCount: Int,
        reference: ReferenceFrame,
    ): Int {
        var cursor = start
        repeat(expectedCount) { index ->
            val point = points?.getOrNull(index)
            if (point == null) {
                cursor += LandmarkFrame.VALUES_PER_LANDMARK
            } else {
                output[cursor] = (point.x - reference.centerX) / reference.scale
                output[cursor + 1] = (point.y - reference.centerY) / reference.scale
                output[cursor + 2] = point.z / reference.scale
                cursor += LandmarkFrame.VALUES_PER_LANDMARK
            }
        }
        return cursor
    }

    private const val LEFT_WRIST = 15
    private const val RIGHT_WRIST = 16

    private fun referenceFrame(pose: List<LandmarkPoint?>): ReferenceFrame {
        val leftShoulder = pose.getOrNull(LEFT_SHOULDER)
        val rightShoulder = pose.getOrNull(RIGHT_SHOULDER)
        val leftHip = pose.getOrNull(LEFT_HIP)
        val rightHip = pose.getOrNull(RIGHT_HIP)
        val leftWrist = pose.getOrNull(LEFT_WRIST)
        val rightWrist = pose.getOrNull(RIGHT_WRIST)

        val shoulderDistance = distance(leftShoulder, rightShoulder)
        val hipDistance = distance(leftHip, rightHip)
        val scale = max(max(shoulderDistance, hipDistance), MIN_SCALE)

        val wrists = listOfNotNull(leftWrist, rightWrist)
        val (centerX, centerY) = if (wrists.isNotEmpty()) {
            Pair(
                wrists.map { it.x }.average().toFloat(),
                wrists.map { it.y }.average().toFloat()
            )
        } else {
            val torsoAnchors = listOfNotNull(leftShoulder, rightShoulder, leftHip, rightHip)
            if (torsoAnchors.isNotEmpty()) {
                Pair(
                    torsoAnchors.map { it.x }.average().toFloat(),
                    torsoAnchors.map { it.y }.average().toFloat()
                )
            } else {
                Pair(0.5f, 0.5f)
            }
        }

        return ReferenceFrame(centerX = centerX, centerY = centerY, scale = scale)
    }

    private fun distance(a: LandmarkPoint?, b: LandmarkPoint?): Float {
        if (a == null || b == null) return 0.0f
        return hypot(a.x - b.x, a.y - b.y)
    }

    private data class ReferenceFrame(
        val centerX: Float,
        val centerY: Float,
        val scale: Float,
    )
}
