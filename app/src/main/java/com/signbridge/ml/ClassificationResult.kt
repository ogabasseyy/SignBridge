package com.signbridge.ml

data class ModelLabel(
    val id: Int,
    val label: String,
)

data class ClassificationResult(
    val labelId: Int,
    val label: String,
    val confidence: Float,
)

data class ClassifierMetadata(
    val inputShape: List<Int>,
    val outputShape: List<Int>,
    val inputDtype: String,
    val outputDtype: String,
)
