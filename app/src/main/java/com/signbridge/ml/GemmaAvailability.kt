package com.signbridge.ml

sealed interface RuntimeStatus {
    val label: String

    data object Available : RuntimeStatus {
        override val label = "available"
    }

    data object Downloadable : RuntimeStatus {
        override val label = "downloadable"
    }

    data object Downloading : RuntimeStatus {
        override val label = "downloading"
    }

    data object Unavailable : RuntimeStatus {
        override val label = "unavailable"
    }

    data class Error(val message: String) : RuntimeStatus {
        override val label = "error"
    }

    data object NotTested : RuntimeStatus {
        override val label = "not tested"
    }
}

enum class PromptRuntime(val displayName: String) {
    PreviewFull("Prompt API Preview FULL"),
    PreviewFast("Prompt API Preview FAST"),
    Stable("Prompt API Stable"),
}

enum class SpeechRuntimeMode(val displayName: String) {
    Basic("Speech Recognition Basic"),
    Advanced("Speech Recognition Advanced"),
}

data class PromptTaskResult(
    val text: String,
    val totalLatencyMillis: Long,
)

data class PromptRuntimeProbe(
    val runtime: PromptRuntime,
    val status: RuntimeStatus,
    val rewrite: PromptTaskResult? = null,
    val condensation: PromptTaskResult? = null,
) {
    val satisfiesGate: Boolean =
        status == RuntimeStatus.Available &&
            rewrite?.text?.isNotBlank() == true &&
            condensation?.text?.isNotBlank() == true
}

data class SpeechRuntimeProbe(
    val mode: SpeechRuntimeMode,
    val status: RuntimeStatus,
) {
    val isReady: Boolean = status == RuntimeStatus.Available
}

data class ApiRealityGate(
    val promptProbes: List<PromptRuntimeProbe>,
    val speechProbes: List<SpeechRuntimeProbe>,
) {
    val selectedPromptRuntime: PromptRuntimeProbe? =
        promptProbes.firstOrNull { it.satisfiesGate }

    val selectedSpeechRuntime: SpeechRuntimeProbe? =
        speechProbes.firstOrNull { it.isReady }

    val canProceedToProductCode: Boolean =
        selectedPromptRuntime != null && selectedSpeechRuntime != null
}
