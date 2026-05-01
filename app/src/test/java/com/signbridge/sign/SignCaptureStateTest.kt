package com.signbridge.sign

import org.junit.Assert.assertEquals
import org.junit.Test

class SignCaptureStateTest {
    @Test
    fun recordsProcessesShowsResultAndResets() {
        val recording = SignCaptureReducer.reduce(SignCaptureState.Idle, SignCaptureAction.StartRecording)
        assertEquals(SignCaptureState.Recording(frameCount = 0), recording)

        val withFrame = SignCaptureReducer.reduce(recording, SignCaptureAction.FrameAccepted)
        assertEquals(SignCaptureState.Recording(frameCount = 1), withFrame)

        val processing = SignCaptureReducer.reduce(withFrame, SignCaptureAction.StopRecording)
        assertEquals(SignCaptureState.Processing, processing)

        val result = SignCaptureReducer.reduce(
            processing,
            SignCaptureAction.ResultReady(gloss = "I am Deaf", confidence = 0.91f),
        )
        assertEquals(SignCaptureState.Result(gloss = "I am Deaf", confidence = 0.91f), result)

        val reset = SignCaptureReducer.reduce(result, SignCaptureAction.Reset)
        assertEquals(SignCaptureState.Idle, reset)
    }

    @Test
    fun stopWithoutFramesReturnsIdle() {
        val state = SignCaptureReducer.reduce(
            SignCaptureState.Recording(frameCount = 0),
            SignCaptureAction.StopRecording,
        )

        assertEquals(SignCaptureState.Idle, state)
    }
}
