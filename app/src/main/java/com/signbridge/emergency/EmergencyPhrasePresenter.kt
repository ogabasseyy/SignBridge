package com.signbridge.emergency

import com.signbridge.domain.Phrase
import com.signbridge.domain.PhraseCatalog
import com.signbridge.tts.Speaker

data class EmergencyPhraseState(
    val phrases: List<Phrase> = PhraseCatalog.emergencyGrid,
)

class EmergencyPhrasePresenter(
    private val speaker: Speaker,
) {
    val state: EmergencyPhraseState = EmergencyPhraseState()

    fun selectPhrase(id: Int) {
        val phrase = state.phrases.firstOrNull { it.id == id } ?: return
        speaker.speak(phrase.text)
    }
}
