package com.signbridge.ml

interface SignInterpreter {
    fun run(input: FloatArray): FloatArray
}

class SignClassifier(
    private val labels: List<ModelLabel>,
    private val interpreter: SignInterpreter,
) {
    fun classify(input: FloatArray): List<ClassificationResult> {
        val scores = interpreter.run(input)
        require(scores.size == labels.size) {
            "Expected ${labels.size} scores but got ${scores.size}"
        }

        return scores.indices
            .sortedByDescending { scores[it] }
            .take(3)
            .map { index ->
                val label = labels[index]
                ClassificationResult(
                    labelId = label.id,
                    label = label.label,
                    confidence = scores[index],
                )
            }
    }
}

class PlaceholderSignInterpreter(
    private val outputSize: Int,
) : SignInterpreter {
    override fun run(input: FloatArray): FloatArray {
        val scores = FloatArray(outputSize) { 0.0f }
        if (scores.isNotEmpty()) {
            scores[scores.lastIndex] = 1.0f
        }
        return scores
    }
}
