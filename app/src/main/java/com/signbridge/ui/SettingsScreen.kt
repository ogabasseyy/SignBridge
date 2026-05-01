package com.signbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.settings.AppSettings
import com.signbridge.settings.ModelPreference
import com.signbridge.ui.components.OfflineBadge
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChange: (AppSettings) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            OfflineBadge()
        }
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        ToggleRow(
            title = "Auto-speak",
            subtitle = "Off unless you turn it on",
            checked = settings.autoSpeakEnabled,
            onCheckedChange = {
                onSettingsChange(settings.copy(autoSpeakEnabled = it).normalized())
            },
        )
        SliderSection(
            title = "Confidence threshold",
            valueLabel = "${(settings.confidenceThreshold * 100).roundToInt()}%",
            value = settings.confidenceThreshold,
            valueRange = AppSettings.MIN_CONFIDENCE_THRESHOLD..AppSettings.MAX_CONFIDENCE_THRESHOLD,
            onValueChange = {
                onSettingsChange(settings.copy(confidenceThreshold = it).normalized())
            },
        )
        SliderSection(
            title = "Voice rate",
            valueLabel = "${(settings.voiceRate * 100).roundToInt()}%",
            value = settings.voiceRate,
            valueRange = AppSettings.MIN_VOICE_RATE..AppSettings.MAX_VOICE_RATE,
            onValueChange = {
                onSettingsChange(settings.copy(voiceRate = it).normalized())
            },
        )
        Text(
            text = "Model",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
        )
        ModelPreference.entries.forEach { preference ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = settings.modelPreference == preference,
                    onClick = {
                        onSettingsChange(settings.copy(modelPreference = preference).normalized())
                    },
                )
                Text(
                    text = preference.label,
                    fontSize = 22.sp,
                )
            }
        }
        ToggleRow(
            title = "Data contribution",
            subtitle = "Off by default",
            checked = settings.dataContributionEnabled,
            onCheckedChange = {
                onSettingsChange(settings.copy(dataContributionEnabled = it).normalized())
            },
        )
        Text(
            text = "SignBridge is not a certified interpreter. For medical, legal, or emergency interactions, always ask for a qualified interpreter.",
            fontSize = 20.sp,
            lineHeight = 26.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = subtitle,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Composable
private fun SliderSection(
    title: String,
    valueLabel: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = valueLabel,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}
