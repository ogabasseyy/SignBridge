package com.signbridge.gemma

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationJsonTest {
    @Test
    fun parsesValidTranslationJson() {
        val result = TranslationJson.parse(
            """{"speakable_text":"Please calm down. I am Deaf.","confidence_level":"high","needs_confirmation":false}""",
            fallbackGlosses = listOf("I am Deaf"),
        )

        assertEquals("Please calm down. I am Deaf.", result.speakableText)
        assertEquals("high", result.confidenceLevel)
        assertFalse(result.needsConfirmation)
    }

    @Test
    fun malformedJsonFallsBackToGlossSentenceAndNeedsConfirmation() {
        val result = TranslationJson.parse(
            "not json",
            fallbackGlosses = listOf("I am Deaf", "Please write it down"),
        )

        assertEquals("I am Deaf. Please write it down.", result.speakableText)
        assertEquals("low", result.confidenceLevel)
        assertTrue(result.needsConfirmation)
    }

    @Test
    fun missingFieldsFallBackSafely() {
        val result = TranslationJson.parse("{}", fallbackGlosses = listOf("Help me"))

        assertEquals("Help me.", result.speakableText)
        assertEquals("low", result.confidenceLevel)
        assertTrue(result.needsConfirmation)
    }
}
