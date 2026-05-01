package com.signbridge.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TtsSpeaker(
    context: Context,
    private val speechRate: Float = 1.0f,
) : TextToSpeech.OnInitListener, Speaker {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            ready = false
            return
        }

        val engine = tts ?: return
        val locale = TtsLocaleSelector(engine.availableLanguages.orEmpty()).selectSpeechLocale()
        engine.language = locale
        engine.setSpeechRate(TtsLocaleSelector.clampSpeechRate(speechRate))
        ready = true
    }

    override fun speak(text: String) {
        if (!ready || text.isBlank()) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "signbridge-${text.hashCode()}")
    }

    fun shutdown() {
        ready = false
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
