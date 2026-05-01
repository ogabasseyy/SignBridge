package com.signbridge.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.signbridge.emergency.EmergencyPhrasePresenter
import com.signbridge.navigation.AppDestination
import com.signbridge.onboarding.DisclaimerAction
import com.signbridge.onboarding.DisclaimerReducer
import com.signbridge.onboarding.DisclaimerState
import com.signbridge.settings.MemorySettingsStore
import com.signbridge.tts.Speaker
import com.signbridge.tts.TtsSpeaker

@Composable
fun SignBridgeApp() {
    val context = LocalContext.current
    val speaker = remember { TtsSpeaker(context) }

    DisposableEffect(Unit) {
        onDispose { speaker.shutdown() }
    }

    SignBridgeAppContent(speaker = speaker)
}

@Composable
fun SignBridgeAppContent(
    speaker: Speaker,
    modifier: Modifier = Modifier,
) {
    var destination by remember { mutableStateOf(AppDestination.Home) }
    var disclaimerState by remember {
        mutableStateOf(DisclaimerState(hasAcceptedDisclaimer = false))
    }
    val settingsStore = remember { MemorySettingsStore() }
    var settings by remember { mutableStateOf(settingsStore.read()) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        if (disclaimerState.shouldShowDisclaimer) {
            OnboardingScreen(
                onAccept = {
                    disclaimerState = DisclaimerReducer.reduce(
                        disclaimerState,
                        DisclaimerAction.Accept,
                    )
                },
                modifier = modifier,
            )
        } else {
            Surface(
                modifier = modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                when (destination) {
                    AppDestination.Home -> HomeScreen(onNavigate = { destination = it })
                    AppDestination.Emergency -> EmergencyScreen(
                        presenter = EmergencyPhrasePresenter(speaker),
                        onBack = { destination = AppDestination.Home },
                    )

                    AppDestination.SignToSpeech -> SignToSpeechScreen(
                        onBack = { destination = AppDestination.Home },
                    )

                    AppDestination.Listen -> ListenScreen(
                        onBack = { destination = AppDestination.Home },
                    )

                    AppDestination.Settings -> SettingsScreen(
                        settings = settings,
                        onSettingsChange = { next ->
                            settings = settingsStore.update { next }
                        },
                        onBack = { destination = AppDestination.Home },
                    )
                }
            }
        }
    }
}
