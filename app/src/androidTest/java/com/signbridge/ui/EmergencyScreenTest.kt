package com.signbridge.ui

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import com.signbridge.emergency.EmergencyPhrasePresenter
import com.signbridge.tts.Speaker
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class EmergencyScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun tappingEmergencyPhraseSpeaksPhrase() {
        val speaker = FakeSpeaker()

        composeRule.setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                EmergencyScreen(
                    presenter = EmergencyPhrasePresenter(speaker),
                    onBack = {},
                )
            }
        }

        composeRule.onNodeWithText("Help me").performClick()

        assertEquals(listOf("Help me"), speaker.spoken)
    }

    private class FakeSpeaker : Speaker {
        val spoken = mutableListOf<String>()

        override fun speak(text: String) {
            spoken += text
        }
    }
}
