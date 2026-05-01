package com.signbridge.ml

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GemmaAvailabilityTest {
    @Test
    fun statusLabelsAreHumanReadable() {
        assertEquals("available", RuntimeStatus.Available.label)
        assertEquals("downloadable", RuntimeStatus.Downloadable.label)
        assertEquals("downloading", RuntimeStatus.Downloading.label)
        assertEquals("unavailable", RuntimeStatus.Unavailable.label)
        assertEquals("error", RuntimeStatus.Error("boom").label)
        assertEquals("not tested", RuntimeStatus.NotTested.label)
    }

    @Test
    fun runtimeSatisfiesGateOnlyWhenBothPromptTasksPass() {
        val passing = PromptRuntimeProbe(
            runtime = PromptRuntime.PreviewFull,
            status = RuntimeStatus.Available,
            rewrite = PromptTaskResult("Please calm down. I am Deaf.", 820),
            condensation = PromptTaskResult("He says he will move the car.", 760),
        )

        val missingCondensation = passing.copy(condensation = null)
        val unavailable = passing.copy(status = RuntimeStatus.Unavailable)

        assertTrue(passing.satisfiesGate)
        assertFalse(missingCondensation.satisfiesGate)
        assertFalse(unavailable.satisfiesGate)
    }

    @Test
    fun apiRealityGateRequiresOnePromptRuntimeAndOneSpeechRuntime() {
        val prompt = PromptRuntimeProbe(
            runtime = PromptRuntime.PreviewFast,
            status = RuntimeStatus.Available,
            rewrite = PromptTaskResult("I am Deaf. Please write it down.", 550),
            condensation = PromptTaskResult("The driver is asking for insurance details.", 610),
        )
        val speech = SpeechRuntimeProbe(
            mode = SpeechRuntimeMode.Basic,
            status = RuntimeStatus.Available,
        )

        val gate = ApiRealityGate(
            promptProbes = listOf(prompt),
            speechProbes = listOf(speech),
        )

        assertTrue(gate.canProceedToProductCode)
        assertEquals(prompt, gate.selectedPromptRuntime)
        assertEquals(speech, gate.selectedSpeechRuntime)
    }
}
