package com.signbridge.gemma

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LiteRtGemmaClientConfigTest {
    @Test
    fun configCarriesOfflineModelSettings() {
        val config = LiteRtGemmaConfig(
            modelPath = "/models/gemma-e2b.litertlm",
            backend = "gpu",
            maxOutputTokens = 160,
            offlineOnly = true,
        )

        assertEquals("/models/gemma-e2b.litertlm", config.modelPath)
        assertEquals("gpu", config.backend)
        assertEquals(160, config.maxOutputTokens)
        assertTrue(config.offlineOnly)
    }
}
