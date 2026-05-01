package com.signbridge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.speech.ListenAction
import com.signbridge.speech.ListenReducer
import com.signbridge.speech.ListenState
import com.signbridge.speech.ReplyCondenser
import com.signbridge.ui.components.OfflineBadge

@Composable
fun ListenScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf<ListenState>(ListenState.Idle) }
    var typedReply by remember { mutableStateOf("") }

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
            TextButton(onClick = onBack) {
                Text("Back")
            }
            OfflineBadge()
        }
        Text(
            text = "Listen",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = listenDisplayText(state),
            fontSize = 32.sp,
            lineHeight = 38.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(
            onClick = {
                state = when (val current = state) {
                    ListenState.Idle,
                    is ListenState.Result -> ListenReducer.reduce(current, ListenAction.StartRecording)

                    ListenState.Recording -> {
                        val transcribing = ListenReducer.reduce(current, ListenAction.StopRecording)
                        ListenReducer.reduce(
                            transcribing,
                            ListenAction.TranscriptReady(
                                transcript = "",
                                condensed = "Please type your reply.",
                            ),
                        )
                    }

                    ListenState.Transcribing -> ListenReducer.reduce(current, ListenAction.Reset)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 92.dp),
        ) {
            Text(
                text = listenButtonText(state),
                fontSize = 28.sp,
            )
        }
        OutlinedTextField(
            value = typedReply,
            onValueChange = { typedReply = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            label = { Text("Type reply") },
        )
        Button(
            onClick = {
                state = ListenReducer.reduce(
                    state,
                    ListenAction.TypedReply(typedReply),
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Show reply")
        }
    }
}

private fun listenDisplayText(state: ListenState): String =
    when (state) {
        ListenState.Idle -> "Ready"
        ListenState.Recording -> "Listening"
        ListenState.Transcribing -> "Transcribing"
        is ListenState.Result -> state.condensed.ifBlank {
            ReplyCondenser.fallbackCondense(state.transcript)
        }
    }

private fun listenButtonText(state: ListenState): String =
    when (state) {
        ListenState.Idle -> "Start listening"
        ListenState.Recording -> "Stop"
        ListenState.Transcribing -> "Reset"
        is ListenState.Result -> "Listen again"
    }
