package com.signbridge.tools

object SignBridgeTools {
    fun detectContext(
        glosses: List<String>,
        timeOfDay: String? = null,
    ): String {
        val text = (glosses.joinToString(" ") + " " + timeOfDay.orEmpty()).lowercase()
        return when {
            listOf("brake", "accident", "insurance", "injured", "fight", "emergency").any { it in text } -> "roadside"
            listOf("doctor", "medicine", "pain", "allergic", "dosage", "clinic").any { it in text } -> "clinic"
            listOf("price", "pay", "restroom", "reply").any { it in text } -> "retail"
            else -> "general"
        }
    }

    fun selectTone(context: String, urgent: Boolean): String =
        when {
            urgent -> "urgent"
            context == "roadside" -> "calm-deescalating"
            else -> "neutral"
        }

    fun extractIntent(transcript: String): String {
        val text = transcript.lowercase()
        return when {
            listOf("okay", "yes", "understand", "agree").any { it in text } -> "agreeing"
            listOf("?", "can you", "what", "where", "why", "show me").any { it in text } -> "asking"
            listOf("no", "not", "refuse", "will not", "can't").any { it in text } -> "refusing"
            else -> "explaining"
        }
    }
}
