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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.signbridge.landmarks.LandmarkFrame
import com.signbridge.ml.ClassificationResult
import com.signbridge.ml.PlaceholderSignInterpreter
import com.signbridge.ml.SignClassifier
import com.signbridge.ml.SignClassifierAssets
import com.signbridge.ml.SlidingWindowBuffer
import com.signbridge.sign.SignCaptureAction
import com.signbridge.sign.SignCaptureReducer
import com.signbridge.sign.SignCaptureState
import com.signbridge.ui.components.OfflineBadge
import com.signbridge.ui.debug.LandmarkOverlay

@Composable
fun SignToSpeechScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
    val classifier = remember(labels) {
        SignClassifier(
            labels = labels,
            interpreter = PlaceholderSignInterpreter(outputSize = labels.size),
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
    val currentCaptureState by rememberUpdatedState(captureState)
    val analyzer = remember {
        FrameAnalyzer(
            onFrameAccepted = {
                val state = currentCaptureState
                if (state is SignCaptureState.Recording) {
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
            PredictionList(predictions)
            CaptureButton(
                state = captureState,
                onClick = {
                    captureState = when (val state = captureState) {
                        SignCaptureState.Idle,
                        is SignCaptureState.Result -> {
                            slidingWindow.reset()
                            predictions = emptyList()
                            SignCaptureReducer.reduce(state, SignCaptureAction.StartRecording)
                        }

                        is SignCaptureState.Recording -> {
                            val next = SignCaptureReducer.reduce(state, SignCaptureAction.StopRecording)
                            predictions = if (slidingWindow.isReady) {
                                classifier.classify(slidingWindow.toTensor())
                            } else {
                                emptyList()
                            }
                            if (predictions.isNotEmpty()) {
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
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun PredictionList(predictions: List<ClassificationResult>) {
    if (predictions.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        predictions.forEachIndexed { index, result ->
            Text(
                text = "${index + 1}. ${result.label} ${(result.confidence * 100).toInt()}%",
                fontSize = 22.sp,
                lineHeight = 26.sp,
                color = if (result.confidence >= 0.65f) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
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
