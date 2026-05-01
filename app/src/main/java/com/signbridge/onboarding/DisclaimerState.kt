package com.signbridge.onboarding

data class DisclaimerState(
    val hasAcceptedDisclaimer: Boolean,
) {
    val shouldShowDisclaimer: Boolean
        get() = !hasAcceptedDisclaimer
}

sealed interface DisclaimerAction {
    data object Accept : DisclaimerAction
}

object DisclaimerReducer {
    fun reduce(
        state: DisclaimerState,
        action: DisclaimerAction,
    ): DisclaimerState =
        when (action) {
            DisclaimerAction.Accept -> state.copy(hasAcceptedDisclaimer = true)
        }
}
