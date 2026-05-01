package com.signbridge.speech

import org.junit.Assert.assertEquals
import org.junit.Test

class ListenStateTest {
    @Test
    fun recordsTranscribesShowsResultAndResets() {
        val recording = ListenReducer.reduce(ListenState.Idle, ListenAction.StartRecording)
        assertEquals(ListenState.Recording, recording)

        val transcribing = ListenReducer.reduce(recording, ListenAction.StopRecording)
        assertEquals(ListenState.Transcribing, transcribing)

        val result = ListenReducer.reduce(
            transcribing,
            ListenAction.TranscriptReady("Move your car.", "Move your car."),
        )
        assertEquals(ListenState.Result("Move your car.", "Move your car."), result)

        val reset = ListenReducer.reduce(result, ListenAction.Reset)
        assertEquals(ListenState.Idle, reset)
    }

    @Test
    fun typedReplyFallbackProducesResultFromIdle() {
        val result = ListenReducer.reduce(
            ListenState.Idle,
            ListenAction.TypedReply("Please show insurance."),
        )

        assertEquals(
            ListenState.Result("Please show insurance.", "Please show insurance."),
            result,
        )
    }
}
