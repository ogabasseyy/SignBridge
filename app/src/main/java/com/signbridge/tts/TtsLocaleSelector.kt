package com.signbridge.tts

import java.util.Locale

class TtsLocaleSelector(
    private val availableLocales: Set<Locale>,
) {
    fun selectSpeechLocale(): Locale =
        when {
            NIGERIAN_ENGLISH in availableLocales -> NIGERIAN_ENGLISH
            Locale.US in availableLocales -> Locale.US
            else -> availableLocales.firstOrNull { it.language == Locale.ENGLISH.language }
                ?: Locale.US
        }

    companion object {
        val NIGERIAN_ENGLISH: Locale = Locale.Builder()
            .setLanguage("en")
            .setRegion("NG")
            .build()

        fun clampSpeechRate(rate: Float): Float =
            rate.coerceIn(0.6f, 1.2f)
    }
}
