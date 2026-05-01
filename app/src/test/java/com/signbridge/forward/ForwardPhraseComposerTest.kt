package com.signbridge.forward

import org.junit.Assert.assertEquals
import org.junit.Test

class ForwardPhraseComposerTest {
    @Test
    fun roadsideGlossesUseDeescalatingTrace() {
        val trace = ForwardPhraseComposer.toolTrace(
            glosses = listOf("Please calm down", "My brakes failed"),
            urgent = false,
        )

        assertEquals("roadside", trace.context)
        assertEquals("calm-deescalating", trace.tone)
    }

    @Test
    fun emergencyGlossesUseUrgentTone() {
        val trace = ForwardPhraseComposer.toolTrace(
            glosses = listOf("Please call emergency services"),
            urgent = true,
        )

        assertEquals("roadside", trace.context)
        assertEquals("urgent", trace.tone)
    }
}
