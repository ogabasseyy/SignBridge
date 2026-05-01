package com.signbridge.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CaptureSessionWriterTest {
    @Test
    fun metadataJsonContainsOnlyLandmarkCaptureMetadata() {
        val metadata = CaptureSessionMetadata(
            phraseId = 1,
            phraseText = "I am Deaf",
            takeNumber = 3,
            lightingTag = "daylight",
            capturedAtEpochMillis = 1_767_200_000_000,
            frameCount = 30,
        )

        val json = CaptureSessionWriter.metadataJson(metadata)

        assertTrue(json.contains("\"phrase_id\":1"))
        assertTrue(json.contains("\"phrase_text\":\"I am Deaf\""))
        assertTrue(json.contains("\"take_number\":3"))
        assertTrue(json.contains("\"frame_count\":30"))
        assertFalse(json.contains("video"))
        assertFalse(json.contains("audio"))
        assertFalse(json.contains("camera"))
        assertFalse(json.contains("person"))
    }
}
