package com.signbridge.gemma

data class TranslationResult(
    val speakableText: String,
    val confidenceLevel: String,
    val needsConfirmation: Boolean,
)

object TranslationJson {
    fun parse(
        json: String,
        fallbackGlosses: List<String>,
    ): TranslationResult {
        val speakableText = parseString(json, "speakable_text")
        val confidence = parseString(json, "confidence_level")
        val needsConfirmation = parseBoolean(json, "needs_confirmation")

        if (speakableText.isNullOrBlank() || confidence.isNullOrBlank() || needsConfirmation == null) {
            return fallback(fallbackGlosses)
        }

        return TranslationResult(
            speakableText = speakableText,
            confidenceLevel = confidence,
            needsConfirmation = needsConfirmation,
        )
    }

    fun fallback(glosses: List<String>): TranslationResult =
        TranslationResult(
            speakableText = glosses.toFallbackSentence(),
            confidenceLevel = "low",
            needsConfirmation = true,
        )

    private fun parseString(json: String, key: String): String? {
        val regex = Regex(""""$key"\s*:\s*"([^"]*)"""")
        return regex.find(json)?.groupValues?.get(1)
    }

    private fun parseBoolean(json: String, key: String): Boolean? {
        val regex = Regex(""""$key"\s*:\s*(true|false)""")
        return regex.find(json)?.groupValues?.get(1)?.toBooleanStrictOrNull()
    }

    private fun List<String>.toFallbackSentence(): String {
        val joined = filter { it.isNotBlank() }
            .joinToString(". ") { it.trim().trimEnd('.', '!', '?') }
        return if (joined.isBlank()) {
            "Please repeat."
        } else {
            "$joined."
        }
    }
}
