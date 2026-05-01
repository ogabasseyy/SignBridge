package com.signbridge.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.signbridge.camera.CameraPreview
import com.signbridge.camera.FrameAnalyzer
import com.signbridge.forward.ForwardPhraseComposer
import com.signbridge.gemma.GemmaClient
import com.signbridge.landmarks.LandmarkFrame
import com.signbridge.ml.ClassificationResult
import com.signbridge.ml.PlaceholderSignInterpreter
import com.signbridge.ml.SignClassifier
import com.signbridge.ml.SignClassifierAssets
import com.signbridge.ml.SlidingWindowBuffer
import com.signbridge.ml.TfliteSignInterpreter
import com.signbridge.sign.SignCaptureAction
import com.signbridge.sign.SignCaptureReducer
import com.signbridge.sign.SignCaptureState
import com.signbridge.tts.Speaker
import com.signbridge.ui.components.OfflineBadge
import com.signbridge.ui.debug.LandmarkOverlay
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.launch

@Composable
fun SignToSpeechScreen(
    onBack: () -> Unit,
    speaker: Speaker,
    gemmaClient: GemmaClient,
    confidenceThreshold: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var hasCameraPermission by remember {
        mutableStateOf(
            context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val labels = remember(context) {
        context.assets.open("signbridge_phrases_v1.labels.json").use { input ->
            SignClassifierAssets.parseLabels(input.bufferedReader().readText())
        }
    }
    val signInterpreter = remember(context, labels) {
        runCatching {
            TfliteSignInterpreter.fromAssets(
                assets = context.assets,
                modelPath = "signbridge_phrases_v1.tflite",
                outputSize = labels.size,
            )
        }.getOrElse {
            PlaceholderSignInterpreter(outputSize = labels.size)
        }
    }
    DisposableEffect(signInterpreter) {
        onDispose {
            (signInterpreter as? AutoCloseable)?.close()
        }
    }
    val classifier = remember(labels, signInterpreter) {
        SignClassifier(
            labels = labels,
            interpreter = signInterpreter,
        )
    }
    val slidingWindow = remember {
        SlidingWindowBuffer(
            windowSize = 30,
            frameSize = LandmarkFrame.TENSOR_SIZE,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted },
    )
    var captureState by remember { mutableStateOf<SignCaptureState>(SignCaptureState.Idle) }
    var latestLandmarks by remember { mutableStateOf<LandmarkFrame?>(null) }
    var predictions by remember { mutableStateOf<List<ClassificationResult>>(emptyList()) }
    var selectedGloss by remember { mutableStateOf<String?>(null) }
    var speakableText by remember { mutableStateOf("") }
    val analyzerRecordingGate = remember { AtomicBoolean(false) }
    val currentCaptureState by rememberUpdatedState(captureState)
    val translateGloss: (String) -> Unit = { gloss ->
        selectedGloss = gloss
        if (gloss == "unknown") {
            speakableText = "Please repeat."
        } else {
            speakableText = "Preparing sentence..."
            val glosses = listOf(gloss)
            val trace = ForwardPhraseComposer.toolTrace(
                glosses = glosses,
                urgent = gloss.isUrgentGloss(),
            )
            scope.launch {
                val result = gemmaClient.reconstructSentence(
                    glosses = glosses,
                    context = trace.context,
                    tone = trace.tone,
                )
                speakableText = result.speakableText
            }
        }
    }
    val analyzer = remember {
        FrameAnalyzer(
            onFrameAccepted = {
                val state = currentCaptureState
                if (analyzerRecordingGate.get() && state is SignCaptureState.Recording) {
                    slidingWindow.add(FloatArray(LandmarkFrame.TENSOR_SIZE))
                    captureState = SignCaptureReducer.reduce(state, SignCaptureAction.FrameAccepted)
                }
            },
            onLandmarks = { latestLandmarks = it },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
            text = "Sign to Speech",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        if (!hasCameraPermission) {
            PermissionPanel(onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            })
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .heightIn(min = 360.dp),
            ) {
                CameraPreview(
                    analyzer = analyzer,
                    modifier = Modifier.matchParentSize(),
                )
                LandmarkOverlay(
                    frame = latestLandmarks,
                    modifier = Modifier.matchParentSize(),
                )
            }
            CaptureStatus(captureState)
            PredictionList(
                predictions = predictions,
                confidenceThreshold = confidenceThreshold,
                onSelect = translateGloss,
            )
            CaptureButton(
                state = captureState,
                onClick = {
                    captureState = when (val state = captureState) {
                        SignCaptureState.Idle,
                        is SignCaptureState.Result -> {
                            slidingWindow.reset()
                            predictions = emptyList()
                            selectedGloss = null
                            speakableText = ""
                            analyzerRecordingGate.set(true)
                            SignCaptureReducer.reduce(state, SignCaptureAction.StartRecording)
                        }

                        is SignCaptureState.Recording -> {
                            analyzerRecordingGate.set(false)
                            val next = SignCaptureReducer.reduce(state, SignCaptureAction.StopRecording)
                            predictions = if (slidingWindow.isReady) {
                                classifier.classify(slidingWindow.toTensor())
                            } else {
                                emptyList()
                            }
                            if (predictions.isNotEmpty()) {
                                translateGloss(predictions.first().label)
                                SignCaptureReducer.reduce(
                                    next,
                                    SignCaptureAction.ResultReady(
                                        gloss = predictions.first().label,
                                        confidence = predictions.first().confidence,
                                    ),
                                )
                            } else {
                                next
                            }
                        }

                        SignCaptureState.Processing -> SignCaptureReducer.reduce(
                            state,
                            SignCaptureAction.Reset,
                        ).also {
                            analyzerRecordingGate.set(false)
                        }
                    }
                },
            )
            TranslationPanel(
                selectedGloss = selectedGloss,
                speakableText = speakableText,
                onSpeak = { speaker.speak(speakableText) },
                onClear = {
                    selectedGloss = null
                    speakableText = ""
                },
            )
        }
    }
}

@Composable
private fun PredictionList(
    predictions: List<ClassificationResult>,
    confidenceThreshold: Float,
    onSelect: (String) -> Unit,
) {
    if (predictions.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Pick the right phrase",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        predictions.forEachIndexed { index, result ->
            Button(
                onClick = { onSelect(result.label) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    text = "${index + 1}. ${result.label} ${(result.confidence * 100).toInt()}%",
                    fontSize = 22.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Center,
                    color = if (result.confidence >= confidenceThreshold) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
                    },
                )
            }
        }
    }
}

@Composable
private fun PermissionPanel(
    onRequestPermission: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Camera permission is needed for signing.",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(onClick = onRequestPermission) {
            Text("Allow camera")
        }
    }
}

@Composable
private fun CaptureStatus(state: SignCaptureState) {
    val text = when (state) {
        SignCaptureState.Idle -> "Ready"
        is SignCaptureState.Recording -> "Recording ${state.frameCount} frames"
        SignCaptureState.Processing -> "Processing"
        is SignCaptureState.Result -> "${state.gloss} (${(state.confidence * 100).toInt()}%)"
    }

    Text(
        text = text,
        fontSize = 28.sp,
        lineHeight = 32.sp,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun CaptureButton(
    state: SignCaptureState,
    onClick: () -> Unit,
) {
    val label = when (state) {
        SignCaptureState.Idle -> "Start signing"
        is SignCaptureState.Recording -> "Stop"
        SignCaptureState.Processing -> "Reset"
        is SignCaptureState.Result -> "Sign again"
    }
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 92.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            text = label,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TranslationPanel(
    selectedGloss: String?,
    speakableText: String,
    onSpeak: () -> Unit,
    onClear: () -> Unit,
) {
    if (speakableText.isBlank()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = selectedGloss?.let { "Selected: $it" } ?: "Selected phrase",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = speakableText,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = onSpeak,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp),
                enabled = speakableText != "Preparing sentence...",
            ) {
                Text(
                    text = "Speak",
                    fontSize = 24.sp,
                )
            }
            TextButton(
                onClick = onClear,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 72.dp),
            ) {
                Text(
                    text = "Clear",
                    fontSize = 24.sp,
                )
            }
        }
    }
}

private fun String.isUrgentGloss(): Boolean {
    val text = lowercase()
    return listOf("injured", "doctor", "emergency", "help").any { it in text }
}
