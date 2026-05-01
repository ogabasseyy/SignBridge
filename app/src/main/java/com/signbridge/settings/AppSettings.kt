package com.signbridge.settings

data class AppSettings(
    val autoSpeakEnabled: Boolean,
    val confidenceThreshold: Float,
    val modelPreference: ModelPreference,
    val dataContributionEnabled: Boolean,
    val voiceRate: Float,
) {
    fun normalized(): AppSettings =
        copy(
            confidenceThreshold = confidenceThreshold.coerceIn(MIN_CONFIDENCE_THRESHOLD, MAX_CONFIDENCE_THRESHOLD),
            voiceRate = voiceRate.coerceIn(MIN_VOICE_RATE, MAX_VOICE_RATE),
        )

    companion object {
        const val MIN_CONFIDENCE_THRESHOLD = 0.5f
        const val MAX_CONFIDENCE_THRESHOLD = 0.95f
        const val MIN_VOICE_RATE = 0.6f
        const val MAX_VOICE_RATE = 1.2f

        fun defaults(): AppSettings =
            AppSettings(
                autoSpeakEnabled = false,
                confidenceThreshold = 0.65f,
                modelPreference = ModelPreference.E4B,
                dataContributionEnabled = false,
                voiceRate = 1.0f,
            )
    }
}

enum class ModelPreference(val label: String) {
    E4B("Gemma 4 E4B"),
    E2B("Gemma 4 E2B");

    companion object {
        fun fromStoredValue(value: String?): ModelPreference =
            entries.firstOrNull { it.name == value } ?: E4B
    }
}
