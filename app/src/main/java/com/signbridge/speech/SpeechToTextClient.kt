package com.signbridge.speech

interface SpeechToTextClient {
    suspend fun listenOnce(): String
}
