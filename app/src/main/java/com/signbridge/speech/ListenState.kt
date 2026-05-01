package com.signbridge.speech

sealed interface ListenState {
    data object Idle : ListenState
    data object Recording : ListenState
    data object Transcribing : ListenState
    data class Result(
        val transcript: String,
        val condensed: String,
    ) : ListenState
}

sealed interface ListenAction {
    data object StartRecording : ListenAction
    data object StopRecording : ListenAction
    data class TranscriptReady(
        val transcript: String,
        val condensed: String,
    ) : ListenAction

    data class TypedReply(val text: String) : ListenAction
    data object Reset : ListenAction
}

object ListenReducer {
    fun reduce(
        state: ListenState,
        action: ListenAction,
    ): ListenState =
        when (action) {
            ListenAction.StartRecording -> ListenState.Recording
            ListenAction.StopRecording -> when (state) {
                ListenState.Recording -> ListenState.Transcribing
                else -> state
            }

            is ListenAction.TranscriptReady -> when (state) {
                ListenState.Transcribing -> ListenState.Result(action.transcript, action.condensed)
                else -> state
            }

            is ListenAction.TypedReply -> ListenState.Result(
                transcript = action.text,
                condensed = ReplyCondenser.fallbackCondense(action.text),
            )

            ListenAction.Reset -> ListenState.Idle
        }
}
