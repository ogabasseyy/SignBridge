package com.signbridge.gemma

object GemmaPromptBuilder {
    fun reconstructionPrompt(
        glosses: List<String>,
        context: String,
        tone: String,
    ): String =
        """
        You are SignBridge, an offline phone assistant for a Deaf signer in Nigeria.
        Rewrite recognized sign glosses into one speakable Nigerian English sentence.
        Do not add facts that are not implied by the glosses.

        Tool trace:
        detect_context=$context
        select_tone=$tone

        Glosses:
        ${glosses.joinToString(separator = "\n") { "- $it" }}

        Return only JSON with this schema:
        {"speakable_text":"...","confidence_level":"high|medium|low","needs_confirmation":true|false}
        """.trimIndent()

    fun replyCondensationPrompt(transcript: String): String =
        """
        Transcribe and condense this hearing person's reply to one sentence the Deaf user can read quickly.
        Preserve key facts. Do not add information.

        Reply:
        $transcript
        """.trimIndent()
}
