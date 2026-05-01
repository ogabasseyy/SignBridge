package com.signbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.navigation.AppDestination
import com.signbridge.ui.components.OfflineBadge
import com.signbridge.ui.components.PrimaryActionCard

@Composable
fun HomeScreen(
    onNavigate: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OfflineBadge()
            TextButton(onClick = { onNavigate(AppDestination.Settings) }) {
                Text("Settings")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "SignBridge",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Choose a mode",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryActionCard(
            label = "Sign to Speech",
            onClick = { onNavigate(AppDestination.SignToSpeech) },
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryActionCard(
            label = "Listen",
            onClick = { onNavigate(AppDestination.Listen) },
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryActionCard(
            label = "Emergency",
            onClick = { onNavigate(AppDestination.Emergency) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
