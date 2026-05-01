package com.signbridge.data

data class CaptureSessionMetadata(
    val phraseId: Int,
    val phraseText: String,
    val takeNumber: Int,
    val lightingTag: String,
    val capturedAtEpochMillis: Long,
    val frameCount: Int,
)

object CaptureSessionWriter {
    fun metadataJson(metadata: CaptureSessionMetadata): String =
        buildString {
            append("{")
            append("\"phrase_id\":").append(metadata.phraseId).append(",")
            append("\"phrase_text\":").append(metadata.phraseText.toJsonString()).append(",")
            append("\"take_number\":").append(metadata.takeNumber).append(",")
            append("\"lighting_tag\":").append(metadata.lightingTag.toJsonString()).append(",")
            append("\"captured_at_epoch_millis\":").append(metadata.capturedAtEpochMillis).append(",")
            append("\"frame_count\":").append(metadata.frameCount)
            append("}")
        }

    private fun String.toJsonString(): String =
        buildString {
            append('"')
            this@toJsonString.forEach { char ->
                when (char) {
                    '\\' -> append("\\\\")
                    '"' -> append("\\\"")
                    '\n' -> append("\\n")
                    '\r' -> append("\\r")
                    '\t' -> append("\\t")
                    else -> append(char)
                }
            }
            append('"')
        }
}
