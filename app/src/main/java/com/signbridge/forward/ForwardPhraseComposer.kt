package com.signbridge.forward

import com.signbridge.tools.SignBridgeTools

data class ForwardToolTrace(
    val context: String,
    val tone: String,
)

object ForwardPhraseComposer {
    fun toolTrace(
        glosses: List<String>,
        urgent: Boolean = false,
    ): ForwardToolTrace {
        val context = SignBridgeTools.detectContext(glosses)
        return ForwardToolTrace(
            context = context,
            tone = SignBridgeTools.selectTone(context, urgent),
        )
    }
}
