package com.signbridge.onboarding

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DisclaimerStateTest {
    @Test
    fun disclaimerIsRequiredBeforeFirstUse() {
        val state = DisclaimerState(hasAcceptedDisclaimer = false)

        assertTrue(state.shouldShowDisclaimer)
    }

    @Test
    fun acknowledgementAllowsMainApp() {
        val state = DisclaimerReducer.reduce(
            state = DisclaimerState(hasAcceptedDisclaimer = false),
            action = DisclaimerAction.Accept,
        )

        assertTrue(state.hasAcceptedDisclaimer)
        assertFalse(state.shouldShowDisclaimer)
    }
}
