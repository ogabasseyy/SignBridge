package com.signbridge.gemma

data class LiteRtGemmaConfig(
    val modelPath: String,
    val backend: String,
    val maxOutputTokens: Int,
    val offlineOnly: Boolean,
)

class LiteRtGemmaClient(
    val config: LiteRtGemmaConfig,
) : GemmaClient {
    override suspend fun reconstructSentence(
        glosses: List<String>,
        context: String,
        tone: String,
    ): TranslationResult =
        TranslationJson.fallback(glosses)

    override suspend fun condenseReply(transcript: String): String =
        transcript.trim().takeIf { it.isNotBlank() } ?: "Please type your reply."
}
