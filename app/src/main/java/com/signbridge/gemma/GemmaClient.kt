package com.signbridge.gemma

interface GemmaClient {
    suspend fun reconstructSentence(
        glosses: List<String>,
        context: String,
        tone: String,
    ): TranslationResult

    suspend fun condenseReply(transcript: String): String
}
