package com.signbridge.spike

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.signbridge.ml.ApiRealityGate
import com.signbridge.ml.PromptRuntimeProbe
import com.signbridge.ml.SpeechRuntimeProbe
import kotlinx.coroutines.launch

@Composable
fun SpikeHomeScreen() {
    val scope = rememberCoroutineScope()
    var spikeState by remember { mutableStateOf<SpikeState>(SpikeState.Idle) }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "SignBridge runtime spike",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "Checks ML Kit Prompt API and ML Kit Speech Recognition availability. Run this on the S24 Ultra in airplane mode for Gate 0.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Button(
                        enabled = spikeState !is SpikeState.Running,
                        onClick = {
                            scope.launch {
                                spikeState = SpikeState.Running
                                spikeState = try {
                                    SpikeState.Done(GemmaRuntimeSpike().runAll())
                                } catch (throwable: Throwable) {
                                    SpikeState.Failed(
                                        throwable.message ?: throwable.javaClass.simpleName,
                                    )
                                }
                            }
                        },
                    ) {
                        Text("Run runtime checks")
                    }
                    RuntimeSpikeContent(spikeState)
                }
            }
        }
    }
}

@Composable
private fun RuntimeSpikeContent(state: SpikeState) {
    when (state) {
        SpikeState.Idle -> Text("Status: not tested")
        SpikeState.Running -> Text("Status: running checks...")
        is SpikeState.Failed -> Text("Status: error - ${state.message}")
        is SpikeState.Done -> RuntimeGateSummary(state.gate)
    }
}

@Composable
private fun RuntimeGateSummary(gate: ApiRealityGate) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = if (gate.canProceedToProductCode) {
                "Gate 0 candidate: PASS"
            } else {
                "Gate 0 candidate: BLOCKED until a prompt runtime and speech runtime are available"
            },
            style = MaterialTheme.typography.titleMedium,
        )
        gate.promptProbes.forEach { probe ->
            PromptProbeText(probe)
        }
        gate.speechProbes.forEach { probe ->
            SpeechProbeText(probe)
        }
    }
}

@Composable
private fun PromptProbeText(probe: PromptRuntimeProbe) {
    val rewrite = probe.rewrite?.let {
        "rewrite ${it.totalLatencyMillis}ms: ${it.text}"
    } ?: "rewrite not run"
    val condensation = probe.condensation?.let {
        "condense ${it.totalLatencyMillis}ms: ${it.text}"
    } ?: "condense not run"

    Text(
        text = "${probe.runtime.displayName}: ${probe.status.label}\n$rewrite\n$condensation",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun SpeechProbeText(probe: SpeechRuntimeProbe) {
    Text(
        text = "${probe.mode.displayName}: ${probe.status.label}",
        style = MaterialTheme.typography.bodyMedium,
    )
}

private sealed interface SpikeState {
    data object Idle : SpikeState
    data object Running : SpikeState
    data class Done(val gate: ApiRealityGate) : SpikeState
    data class Failed(val message: String) : SpikeState
}
