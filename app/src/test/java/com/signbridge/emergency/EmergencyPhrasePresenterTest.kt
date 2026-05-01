package com.signbridge.emergency

import com.signbridge.tts.Speaker
import org.junit.Assert.assertEquals
import org.junit.Test

class EmergencyPhrasePresenterTest {
    @Test
    fun exposesEmergencyPhrasesInRequiredOrder() {
        val presenter = EmergencyPhrasePresenter(FakeSpeaker())

        assertEquals(
            listOf(
                "Help me",
                "I am Deaf",
                "Please call emergency services",
                "I am injured",
                "Please write it down",
                "I cannot hear you",
            ),
            presenter.state.phrases.map { it.text },
        )
    }

    @Test
    fun selectingPhraseSpeaksSelectedText() {
        val speaker = FakeSpeaker()
        val presenter = EmergencyPhrasePresenter(speaker)

        presenter.selectPhrase(23)

        assertEquals(listOf("Help me"), speaker.spoken)
    }

    private class FakeSpeaker : Speaker {
        val spoken = mutableListOf<String>()

        override fun speak(text: String) {
            spoken += text
        }
    }
}
