package com.signbridge.ml

import org.junit.Assert.assertEquals
import org.junit.Test

class SignClassifierInputTest {
    @Test
    fun reshapesFlatWindowToInterpreterInput() {
        val flat = FloatArray(30 * 1629) { it.toFloat() }

        val input = SignClassifierInput.reshape(flat)

        assertEquals(1, input.size)
        assertEquals(30, input[0].size)
        assertEquals(1629, input[0][0].size)
        assertEquals(0.0f, input[0][0][0], 0.0001f)
        assertEquals(1628.0f, input[0][0][1628], 0.0001f)
        assertEquals(1629.0f, input[0][1][0], 0.0001f)
    }
}
