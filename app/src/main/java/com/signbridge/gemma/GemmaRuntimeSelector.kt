package com.signbridge.gemma

import com.signbridge.ml.PromptRuntime
import com.signbridge.ml.PromptRuntimeProbe

object GemmaModelSelectionPolicy {
    fun select(probes: List<PromptRuntimeProbe>): PromptRuntimeProbe? {
        val byRuntime = probes.associateBy { it.runtime }
        return listOf(
            PromptRuntime.PreviewFull,
            PromptRuntime.PreviewFast,
            PromptRuntime.Stable,
        ).firstNotNullOfOrNull { runtime ->
            byRuntime[runtime]?.takeIf { it.satisfiesGate }
        }
    }
}

sealed interface GemmaRuntimeDecision {
    data class MlKit(val runtime: PromptRuntime) : GemmaRuntimeDecision
    data object LiteRt : GemmaRuntimeDecision
    data object HardStop : GemmaRuntimeDecision
}

object GemmaRuntimeSelector {
    fun select(
        promptProbes: List<PromptRuntimeProbe>,
        liteRtAvailable: Boolean,
    ): GemmaRuntimeDecision {
        val mlKit = GemmaModelSelectionPolicy.select(promptProbes)
        return when {
            mlKit != null -> GemmaRuntimeDecision.MlKit(mlKit.runtime)
            liteRtAvailable -> GemmaRuntimeDecision.LiteRt
            else -> GemmaRuntimeDecision.HardStop
        }
    }
}
