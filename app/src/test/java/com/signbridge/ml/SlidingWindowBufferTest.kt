package com.signbridge.ml

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SlidingWindowBufferTest {
    @Test
    fun becomesReadyAfterThirtyFrames() {
        val buffer = SlidingWindowBuffer(windowSize = 30, frameSize = 3)

        repeat(29) { buffer.add(floatArrayOf(1f, 2f, 3f)) }
        assertFalse(buffer.isReady)

        buffer.add(floatArrayOf(4f, 5f, 6f))
        assertTrue(buffer.isReady)
    }

    @Test
    fun resetClearsReadiness() {
        val buffer = SlidingWindowBuffer(windowSize = 2, frameSize = 1)
        buffer.add(floatArrayOf(1f))
        buffer.add(floatArrayOf(2f))

        buffer.reset()

        assertFalse(buffer.isReady)
    }

    @Test
    fun overflowKeepsMostRecentFramesInOrder() {
        val buffer = SlidingWindowBuffer(windowSize = 3, frameSize = 1)

        buffer.add(floatArrayOf(1f))
        buffer.add(floatArrayOf(2f))
        buffer.add(floatArrayOf(3f))
        buffer.add(floatArrayOf(4f))

        assertArrayEquals(floatArrayOf(2f, 3f, 4f), buffer.toTensor(), 0.0f)
    }
}
