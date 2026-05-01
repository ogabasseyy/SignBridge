package com.signbridge.speech

object ReplyCondenser {
    fun prompt(transcript: String): String =
        """
        Condense this hearing person's reply into one sentence.
        Preserve key facts. Do not add information.

        Reply:
        $transcript
        """.trimIndent()

    fun fallbackCondense(transcript: String): String {
        val trimmed = transcript.trim()
        if (trimmed.isBlank()) return "Please type your reply."
        val sentenceEnd = trimmed.indexOfFirst { it == '.' || it == '!' || it == '?' }
        return if (sentenceEnd >= 0) {
            trimmed.substring(0, sentenceEnd + 1)
        } else {
            "$trimmed."
        }
    }
}
