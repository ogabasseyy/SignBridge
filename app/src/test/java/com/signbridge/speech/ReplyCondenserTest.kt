package com.signbridge.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReplyCondenserTest {
    @Test
    fun promptPreservesFactsAndForbidsAddedInformation() {
        val prompt = ReplyCondenser.prompt("Move your car to the side and show insurance.")

        assertTrue(prompt.contains("one sentence"))
        assertTrue(prompt.contains("Preserve key facts"))
        assertTrue(prompt.contains("Do not add information"))
        assertTrue(prompt.contains("show insurance"))
    }

    @Test
    fun fallbackCondensesToFirstReadableSentence() {
        val result = ReplyCondenser.fallbackCondense(
            "Move your car to the side. Then show your insurance.",
        )

        assertEquals("Move your car to the side.", result)
    }
}
