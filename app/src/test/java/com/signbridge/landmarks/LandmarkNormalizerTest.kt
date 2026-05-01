package com.signbridge.landmarks

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class LandmarkNormalizerTest {
    @Test
    fun returnsStableTensorShape() {
        val frame = LandmarkFrame(
            pose = poseWithTorso(),
            leftHand = points(21),
            rightHand = points(21),
            face = points(468),
        )

        val tensor = LandmarkNormalizer.normalize(frame)

        assertEquals(543 * 3, tensor.size)
    }

    @Test
    fun centersAndScalesAroundTorso() {
        val pose = poseWithTorso()
        val leftHand = points(21, LandmarkPoint(x = 0.6f, y = 0.5f, z = 0.0f))
        val frame = LandmarkFrame(
            pose = pose,
            leftHand = leftHand,
            rightHand = null,
            face = null,
        )

        val tensor = LandmarkNormalizer.normalize(frame)
        val leftHandStart = 33 * 3

        assertEquals(0.5f, tensor[leftHandStart], 0.0001f)
        assertEquals(-0.5f, tensor[leftHandStart + 1], 0.0001f)
        assertEquals(0.0f, tensor[leftHandStart + 2], 0.0001f)
    }

    @Test
    fun zeroPadsMissingHands() {
        val frame = LandmarkFrame(
            pose = poseWithTorso(),
            leftHand = null,
            rightHand = null,
            face = null,
        )

        val tensor = LandmarkNormalizer.normalize(frame)
        val leftHandStart = 33 * 3
        val handValues = tensor.copyOfRange(leftHandStart, leftHandStart + (21 * 3))

        assertArrayEquals(FloatArray(21 * 3), handValues, 0.0f)
    }

    private fun poseWithTorso(): List<LandmarkPoint?> {
        val points = MutableList<LandmarkPoint?>(33) { LandmarkPoint(0.5f, 0.6f, 0.0f) }
        points[11] = LandmarkPoint(0.4f, 0.5f, 0.0f)
        points[12] = LandmarkPoint(0.6f, 0.5f, 0.0f)
        points[23] = LandmarkPoint(0.4f, 0.7f, 0.0f)
        points[24] = LandmarkPoint(0.6f, 0.7f, 0.0f)
        return points
    }

    private fun points(
        count: Int,
        point: LandmarkPoint = LandmarkPoint(0.5f, 0.6f, 0.0f),
    ): List<LandmarkPoint?> = List(count) { point }
}
