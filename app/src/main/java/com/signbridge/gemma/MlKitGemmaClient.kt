package com.signbridge.gemma

import com.signbridge.ml.PromptRuntime

class MlKitGemmaClient(
    val runtime: PromptRuntime,
) : GemmaClient {
    override suspend fun reconstructSentence(
        glosses: List<String>,
        context: String,
        tone: String,
    ): TranslationResult {
        val prompt = GemmaPromptBuilder.reconstructionPrompt(glosses, context, tone)
        return TranslationJson.parse(
            json = generate(prompt, glosses),
            fallbackGlosses = glosses,
        )
    }

    override suspend fun condenseReply(transcript: String): String {
        val prompt = GemmaPromptBuilder.replyCondensationPrompt(transcript)
        return generate(prompt, listOf(transcript)).ifBlank {
            transcript.trim()
        }
    }

    private fun generate(
        prompt: String,
        fallbackGlosses: List<String>,
    ): String {
        // Gate 0 decides whether this class can be replaced with live Prompt API generation.
        if (prompt.isBlank()) return ""
        return """{"speakable_text":"${fallbackGlosses.joinToString(". ")}.","confidence_level":"low","needs_confirmation":true}"""
    }
}
