package com.signbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.emergency.EmergencyPhrasePresenter
import com.signbridge.navigation.AppDestination
import com.signbridge.tts.Speaker
import com.signbridge.tts.TtsSpeaker
import com.signbridge.ui.components.OfflineBadge

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

    MaterialTheme(colorScheme = darkColorScheme()) {
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

                AppDestination.SignToSpeech -> PlaceholderScreen(
                    title = "Sign to Speech",
                    onBack = { destination = AppDestination.Home },
                )

                AppDestination.Listen -> PlaceholderScreen(
                    title = "Listen",
                    onBack = { destination = AppDestination.Home },
                )

                AppDestination.Settings -> PlaceholderScreen(
                    title = "Settings",
                    onBack = { destination = AppDestination.Home },
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        TextButton(onClick = onBack) {
            Text("Back")
        }
        OfflineBadge()
        Text(
            text = title,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "In progress",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
