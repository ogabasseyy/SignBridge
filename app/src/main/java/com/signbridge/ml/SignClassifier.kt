package com.signbridge.ml

import android.content.res.AssetManager
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

interface SignInterpreter {
    fun run(input: FloatArray): FloatArray
}

object SignClassifierInput {
    fun reshape(input: FloatArray): Array<Array<FloatArray>> {
        require(input.size == 30 * 1629) {
            "Expected ${30 * 1629} input values but got ${input.size}"
        }
        return Array(1) {
            Array(30) { frame ->
                FloatArray(1629) { landmark ->
                    input[(frame * 1629) + landmark]
                }
            }
        }
    }
}

class SignClassifier(
    private val labels: List<ModelLabel>,
    private val interpreter: SignInterpreter,
    private val smoothingAlpha: Float = 0.5f,
) {
    private var smoothedScores: FloatArray? = null

    fun classify(input: FloatArray): List<ClassificationResult> {
        val scores = interpreter.run(input)
        require(scores.size == labels.size) {
            "Expected ${labels.size} scores but got ${scores.size}"
        }

        if (smoothedScores == null) {
            smoothedScores = scores.clone()
        } else {
            val current = smoothedScores!!
            for (i in scores.indices) {
                current[i] = (smoothingAlpha * scores[i]) + ((1.0f - smoothingAlpha) * current[i])
            }
        }

        val displayScores = smoothedScores!!
        return displayScores.indices
            .sortedByDescending { displayScores[it] }
            .take(3)
            .map { index ->
                val label = labels[index]
                ClassificationResult(
                    labelId = label.id,
                    label = label.label,
                    confidence = displayScores[index],
                )
            }
    }

    fun reset() {
        smoothedScores = null
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

class TfliteSignInterpreter private constructor(
    modelBuffer: ByteBuffer,
    private val outputSize: Int,
) : SignInterpreter, AutoCloseable {
    private val interpreter = Interpreter(modelBuffer)

    override fun run(input: FloatArray): FloatArray {
        val output = Array(1) { FloatArray(outputSize) }
        interpreter.run(SignClassifierInput.reshape(input), output)
        return output[0]
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        fun fromAssets(
            assets: AssetManager,
            modelPath: String,
            outputSize: Int,
        ): TfliteSignInterpreter {
            val bytes = assets.open(modelPath).use { input ->
                input.readBytes()
            }
            val buffer = ByteBuffer.allocateDirect(bytes.size)
                .order(ByteOrder.nativeOrder())
            buffer.put(bytes)
            buffer.rewind()
            return TfliteSignInterpreter(buffer, outputSize)
        }
    }
}
