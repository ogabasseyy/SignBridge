package com.signbridge.tools

import org.junit.Assert.assertEquals
import org.junit.Test

class SignBridgeToolsTest {
    @Test
    fun detectContextFromGlosses() {
        assertEquals("roadside", SignBridgeTools.detectContext(listOf("My brakes failed")))
        assertEquals("clinic", SignBridgeTools.detectContext(listOf("I need medicine")))
        assertEquals("retail", SignBridgeTools.detectContext(listOf("Please show me the price")))
        assertEquals("general", SignBridgeTools.detectContext(listOf("Please write it down")))
    }

    @Test
    fun selectToneFromContextAndUrgency() {
        assertEquals("calm-deescalating", SignBridgeTools.selectTone("roadside", urgent = false))
        assertEquals("urgent", SignBridgeTools.selectTone("clinic", urgent = true))
        assertEquals("neutral", SignBridgeTools.selectTone("retail", urgent = false))
    }

    @Test
    fun extractIntentFromTranscript() {
        assertEquals("agreeing", SignBridgeTools.extractIntent("Okay, I understand."))
        assertEquals("asking", SignBridgeTools.extractIntent("Can you show me your insurance?"))
        assertEquals("refusing", SignBridgeTools.extractIntent("No, I will not move."))
        assertEquals("explaining", SignBridgeTools.extractIntent("The road is blocked because of traffic."))
    }
}
