package com.signbridge.llm

interface LlmClient {
    suspend fun generate(prompt: String): Result<String>
}

class GemmaCondenser(private val llmClient: LlmClient) {

    suspend fun condense(phrases: List<String>): Result<String> {
        if (phrases.isEmpty()) {
            return Result.success("")
        }

        if (phrases.size == 1) {
            return Result.success(phrases.first())
        }

        val prompt = buildPrompt(phrases)
        return llmClient.generate(prompt)
    }

    private fun buildPrompt(phrases: List<String>): String {
        val joinedPhrases = phrases.joinToString(", ")
        return """
            You are an expert Nigerian Sign Language (NSL) interpreter.
            Convert the following sequence of isolated signed phrases into a single, natural conversational sentence in Nigerian English.
            
            Strict Constraints:
            1. ONLY output the natural sentence. Do not add any conversational filler, greetings, or explanations.
            2. Do not add outside information. Use only the meaning provided by the phrases.
            3. If the meaning is ambiguous, provide the most direct, literal translation possible.
            
            Input phrases: $joinedPhrases
            
            Translation:
        """.trimIndent()
    }
}
