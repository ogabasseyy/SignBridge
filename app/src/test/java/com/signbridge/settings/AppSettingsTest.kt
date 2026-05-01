package com.signbridge.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AppSettingsTest {
    @Test
    fun defaultsArePrivacyFirstAndDemoSafe() {
        val settings = AppSettings.defaults()

        assertFalse(settings.autoSpeakEnabled)
        assertEquals(0.65f, settings.confidenceThreshold, 0.0001f)
        assertEquals(ModelPreference.E4B, settings.modelPreference)
        assertFalse(settings.dataContributionEnabled)
        assertEquals(1.0f, settings.voiceRate, 0.0001f)
    }

    @Test
    fun clampsUnsafeNumericValues() {
        val settings = AppSettings(
            autoSpeakEnabled = true,
            confidenceThreshold = 0.1f,
            modelPreference = ModelPreference.E2B,
            dataContributionEnabled = true,
            voiceRate = 5.0f,
        ).normalized()

        assertEquals(0.5f, settings.confidenceThreshold, 0.0001f)
        assertEquals(1.2f, settings.voiceRate, 0.0001f)
    }

    @Test
    fun invalidStoredModelFallsBackToE4B() {
        assertEquals(ModelPreference.E4B, ModelPreference.fromStoredValue("unknown"))
        assertEquals(ModelPreference.E2B, ModelPreference.fromStoredValue("E2B"))
    }
}
