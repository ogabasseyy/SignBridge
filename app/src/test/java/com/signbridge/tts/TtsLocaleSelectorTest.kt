package com.signbridge.tts

import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class TtsLocaleSelectorTest {
    @Test
    fun prefersNigerianEnglishWhenAvailable() {
        val nigerianEnglish = Locale.Builder()
            .setLanguage("en")
            .setRegion("NG")
            .build()
        val selector = TtsLocaleSelector(
            availableLocales = setOf(Locale.US, nigerianEnglish),
        )

        assertEquals(nigerianEnglish, selector.selectSpeechLocale())
    }

    @Test
    fun fallsBackToUsEnglishWhenNigerianEnglishIsUnavailable() {
        val selector = TtsLocaleSelector(
            availableLocales = setOf(Locale.US),
        )

        assertEquals(Locale.US, selector.selectSpeechLocale())
    }

    @Test
    fun clampsVoiceRateToReadableRange() {
        assertEquals(0.6f, TtsLocaleSelector.clampSpeechRate(0.2f))
        assertEquals(1.0f, TtsLocaleSelector.clampSpeechRate(1.0f))
        assertEquals(1.2f, TtsLocaleSelector.clampSpeechRate(1.8f))
    }
}
