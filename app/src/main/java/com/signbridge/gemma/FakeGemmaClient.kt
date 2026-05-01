package com.signbridge.gemma

class FakeGemmaClient : GemmaClient {
    override suspend fun reconstructSentence(
        glosses: List<String>,
        context: String,
        tone: String,
    ): TranslationResult =
        TranslationJson.fallback(glosses).copy(
            confidenceLevel = if (glosses.isNotEmpty()) "medium" else "low",
            needsConfirmation = true,
        )

    override suspend fun condenseReply(transcript: String): String =
        transcript.trim().lineSequence().firstOrNull().orEmpty().ifBlank {
            "Please type your reply."
        }
}
