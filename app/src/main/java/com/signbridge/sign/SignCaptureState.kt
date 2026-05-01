package com.signbridge.sign

sealed interface SignCaptureState {
    data object Idle : SignCaptureState
    data class Recording(val frameCount: Int) : SignCaptureState
    data object Processing : SignCaptureState
    data class Result(val gloss: String, val confidence: Float) : SignCaptureState
}

sealed interface SignCaptureAction {
    data object StartRecording : SignCaptureAction
    data object FrameAccepted : SignCaptureAction
    data object StopRecording : SignCaptureAction
    data class ResultReady(val gloss: String, val confidence: Float) : SignCaptureAction
    data object Reset : SignCaptureAction
}

object SignCaptureReducer {
    fun reduce(
        state: SignCaptureState,
        action: SignCaptureAction,
    ): SignCaptureState =
        when (action) {
            SignCaptureAction.StartRecording -> SignCaptureState.Recording(frameCount = 0)
            SignCaptureAction.FrameAccepted -> when (state) {
                is SignCaptureState.Recording -> state.copy(frameCount = state.frameCount + 1)
                else -> state
            }

            SignCaptureAction.StopRecording -> when (state) {
                is SignCaptureState.Recording -> {
                    if (state.frameCount > 0) SignCaptureState.Processing else SignCaptureState.Idle
                }

                else -> state
            }

            is SignCaptureAction.ResultReady -> when (state) {
                SignCaptureState.Processing -> SignCaptureState.Result(
                    gloss = action.gloss,
                    confidence = action.confidence,
                )

                else -> state
            }

            SignCaptureAction.Reset -> SignCaptureState.Idle
        }
}
