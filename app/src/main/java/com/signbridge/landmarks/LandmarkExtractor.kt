package com.signbridge.landmarks

import androidx.camera.core.ImageProxy

interface LandmarkExtractor {
    fun extract(image: ImageProxy): LandmarkFrame?
}

class NoOpLandmarkExtractor : LandmarkExtractor {
    override fun extract(image: ImageProxy): LandmarkFrame? = null
}
