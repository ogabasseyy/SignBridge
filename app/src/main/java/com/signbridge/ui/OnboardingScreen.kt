package com.signbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.ui.components.OfflineBadge

@Composable
fun OnboardingScreen(
    onAccept: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            OfflineBadge()
            Text(
                text = "SignBridge",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "SignBridge is not a certified interpreter. For medical, legal, or emergency interactions, always ask for a qualified interpreter.",
                fontSize = 28.sp,
                lineHeight = 34.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "No video or audio leaves this device in the hackathon build.",
                fontSize = 22.sp,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 86.dp),
            ) {
                Text(
                    text = "I understand",
                    fontSize = 28.sp,
                )
            }
        }
    }
}
