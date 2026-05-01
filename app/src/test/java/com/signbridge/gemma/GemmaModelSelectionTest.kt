package com.signbridge.gemma

import com.signbridge.ml.PromptRuntime
import com.signbridge.ml.PromptRuntimeProbe
import com.signbridge.ml.PromptTaskResult
import com.signbridge.ml.RuntimeStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class GemmaModelSelectionTest {
    @Test
    fun selectsPreviewFullThenFastThenStable() {
        val stable = available(PromptRuntime.Stable)
        val fast = available(PromptRuntime.PreviewFast)
        val full = available(PromptRuntime.PreviewFull)

        assertEquals(full, GemmaModelSelectionPolicy.select(listOf(stable, fast, full)))
        assertEquals(fast, GemmaModelSelectionPolicy.select(listOf(stable, fast)))
        assertEquals(stable, GemmaModelSelectionPolicy.select(listOf(stable)))
    }

    @Test
    fun unavailableModelsReturnNull() {
        val probe = PromptRuntimeProbe(
            runtime = PromptRuntime.PreviewFull,
            status = RuntimeStatus.Unavailable,
        )

        assertEquals(null, GemmaModelSelectionPolicy.select(listOf(probe)))
    }

    private fun available(runtime: PromptRuntime): PromptRuntimeProbe =
        PromptRuntimeProbe(
            runtime = runtime,
            status = RuntimeStatus.Available,
            rewrite = PromptTaskResult("ok", 1),
            condensation = PromptTaskResult("ok", 1),
        )
}
