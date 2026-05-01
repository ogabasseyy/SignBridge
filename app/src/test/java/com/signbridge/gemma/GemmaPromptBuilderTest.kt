package com.signbridge.gemma

import org.junit.Assert.assertTrue
import org.junit.Test

class GemmaPromptBuilderTest {
    @Test
    fun reconstructionPromptContainsGlossesToolTraceSchemaAndSafetyRules() {
        val prompt = GemmaPromptBuilder.reconstructionPrompt(
            glosses = listOf("I am Deaf", "It was an accident", "My brakes failed"),
            context = "roadside",
            tone = "calm-deescalating",
        )

        assertTrue(prompt.contains("I am Deaf"))
        assertTrue(prompt.contains("My brakes failed"))
        assertTrue(prompt.contains("detect_context=roadside"))
        assertTrue(prompt.contains("select_tone=calm-deescalating"))
        assertTrue(prompt.contains("Do not add facts"))
        assertTrue(prompt.contains("Nigerian English"))
        assertTrue(prompt.contains("speakable_text"))
        assertTrue(prompt.contains("needs_confirmation"))
    }

    @Test
    fun replyCondensationPromptPreservesFactsAndLimitsOneSentence() {
        val prompt = GemmaPromptBuilder.replyCondensationPrompt("Move your car and show insurance.")

        assertTrue(prompt.contains("one sentence"))
        assertTrue(prompt.contains("Preserve key facts"))
        assertTrue(prompt.contains("Do not add information"))
        assertTrue(prompt.contains("Move your car"))
    }
}
