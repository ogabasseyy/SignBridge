package com.signbridge.ml

import com.signbridge.domain.PhraseCatalog
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignClassifierTensorContractTest {
    @Test
    fun labelsAssetMatchesPhraseCatalogPlusUnknown() {
        val labelsFile = File("src/main/assets/signbridge_phrases_v1.labels.json")
        assertTrue(labelsFile.exists())

        val labels = SignClassifierAssets.parseLabels(labelsFile.readText())
        val expected = PhraseCatalog.all.map { ModelLabel(id = it.id, label = it.text) } +
            ModelLabel(id = 26, label = "unknown")

        assertEquals(expected, labels)
    }

    @Test
    fun modelAndMetadataAssetsDeclareExpectedTensorContract() {
        assertTrue(File("src/main/assets/signbridge_phrases_v1.tflite").exists())

        val metadataFile = File("src/main/assets/signbridge_phrases_v1.metadata.json")
        assertTrue(metadataFile.exists())

        val metadata = SignClassifierAssets.parseMetadata(metadataFile.readText())

        assertEquals(listOf(1, 30, 1629), metadata.inputShape)
        assertEquals(listOf(1, 26), metadata.outputShape)
        assertEquals("float32", metadata.inputDtype)
        assertEquals("float32", metadata.outputDtype)
    }

    @Test
    fun fakeInterpreterResultsMapToTopThreeLabels() {
        val classifier = SignClassifier(
            labels = listOf(
                ModelLabel(1, "I am Deaf"),
                ModelLabel(2, "Please calm down"),
                ModelLabel(3, "Please write it down"),
                ModelLabel(26, "unknown"),
            ),
            interpreter = FakeInterpreter(floatArrayOf(0.2f, 0.8f, 0.4f, 0.1f)),
        )

        val results = classifier.classify(FloatArray(30 * 1629))

        assertEquals("Please calm down", results[0].label)
        assertEquals("Please write it down", results[1].label)
        assertEquals("I am Deaf", results[2].label)
    }

    private class FakeInterpreter(
        private val scores: FloatArray,
    ) : SignInterpreter {
        override fun run(input: FloatArray): FloatArray = scores
    }
}
