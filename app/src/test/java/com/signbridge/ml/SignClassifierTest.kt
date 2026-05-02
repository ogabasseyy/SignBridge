package com.signbridge.ml

import org.junit.Assert.assertEquals
import org.junit.Test

class SignClassifierTest {

    @Test
    fun `exponential smoothing blends previous and current scores`() {
        val labels = listOf(
            ModelLabel(1, "A"),
            ModelLabel(2, "B")
        )

        val interpreter = object : SignInterpreter {
            var currentOutput = floatArrayOf()
            override fun run(input: FloatArray): FloatArray {
                return currentOutput
            }
        }

        // alpha = 0.5
        val classifier = SignClassifier(labels, interpreter, 0.5f)

        // First frame: returns exactly the raw scores
        interpreter.currentOutput = floatArrayOf(1.0f, 0.0f)
        val result1 = classifier.classify(FloatArray(0))
        assertEquals("A", result1[0].label)
        assertEquals(1.0f, result1[0].confidence, 0.001f)

        // Second frame: raw score is 0.0 for A, 1.0 for B
        // With alpha=0.5, smoothed A should be 0.5*0 + 0.5*1.0 = 0.5
        // Smoothed B should be 0.5*1.0 + 0.5*0.0 = 0.5
        interpreter.currentOutput = floatArrayOf(0.0f, 1.0f)
        val result2 = classifier.classify(FloatArray(0))
        
        val aScore = result2.find { it.label == "A" }?.confidence ?: 0f
        val bScore = result2.find { it.label == "B" }?.confidence ?: 0f
        
        assertEquals(0.5f, aScore, 0.001f)
        assertEquals(0.5f, bScore, 0.001f)
    }
}
