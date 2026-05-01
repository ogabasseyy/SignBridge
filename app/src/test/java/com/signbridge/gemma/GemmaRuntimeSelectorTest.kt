package com.signbridge.gemma

import com.signbridge.ml.PromptRuntime
import com.signbridge.ml.PromptRuntimeProbe
import com.signbridge.ml.PromptTaskResult
import com.signbridge.ml.RuntimeStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class GemmaRuntimeSelectorTest {
    @Test
    fun usesMlKitWhenPromptRuntimeIsAvailable() {
        val selected = GemmaRuntimeSelector.select(
            promptProbes = listOf(available(PromptRuntime.PreviewFast)),
            liteRtAvailable = true,
        )

        assertEquals(GemmaRuntimeDecision.MlKit(PromptRuntime.PreviewFast), selected)
    }

    @Test
    fun usesLiteRtWhenMlKitUnavailable() {
        val selected = GemmaRuntimeSelector.select(
            promptProbes = emptyList(),
            liteRtAvailable = true,
        )

        assertEquals(GemmaRuntimeDecision.LiteRt, selected)
    }

    @Test
    fun hardStopsWhenNoGemmaRuntimeWorks() {
        val selected = GemmaRuntimeSelector.select(
            promptProbes = emptyList(),
            liteRtAvailable = false,
        )

        assertEquals(GemmaRuntimeDecision.HardStop, selected)
    }

    private fun available(runtime: PromptRuntime): PromptRuntimeProbe =
        PromptRuntimeProbe(
            runtime = runtime,
            status = RuntimeStatus.Available,
            rewrite = PromptTaskResult("ok", 1),
            condensation = PromptTaskResult("ok", 1),
        )
}
