package com.signbridge.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.signbridge.landmarks.LandmarkExtractor
import com.signbridge.landmarks.LandmarkFrame
import com.signbridge.landmarks.NoOpLandmarkExtractor

class FrameAnalyzer(
    private val landmarkExtractor: LandmarkExtractor = NoOpLandmarkExtractor(),
    private val onFrameAccepted: () -> Unit,
    private val onLandmarks: (LandmarkFrame) -> Unit,
) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        try {
            landmarkExtractor.extract(image)?.let(onLandmarks)
            onFrameAccepted()
        } finally {
            image.close()
        }
    }
}
