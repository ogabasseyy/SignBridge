package com.signbridge.ml

object SignClassifierAssets {
    fun parseLabels(json: String): List<ModelLabel> {
        val regex = Regex(
            pattern = """\{\s*"id"\s*:\s*(\d+)\s*,\s*"label"\s*:\s*"([^"]+)"\s*\}""",
            options = setOf(RegexOption.MULTILINE),
        )
        return regex.findAll(json)
            .map { match ->
                ModelLabel(
                    id = match.groupValues[1].toInt(),
                    label = match.groupValues[2],
                )
            }
            .toList()
    }

    fun parseMetadata(json: String): ClassifierMetadata =
        ClassifierMetadata(
            inputShape = parseShape(json, "input_shape"),
            outputShape = parseShape(json, "output_shape"),
            inputDtype = parseString(json, "input_dtype"),
            outputDtype = parseString(json, "output_dtype"),
        )

    private fun parseShape(json: String, key: String): List<Int> {
        val regex = Regex(
            pattern = """"$key"\s*:\s*\[([^\]]+)]""",
            options = setOf(RegexOption.MULTILINE),
        )
        val values = regex.find(json)?.groupValues?.get(1)
            ?: error("Missing shape key: $key")
        return values.split(",").map { it.trim().toInt() }
    }

    private fun parseString(json: String, key: String): String {
        val regex = Regex(""""$key"\s*:\s*"([^"]+)"""")
        return regex.find(json)?.groupValues?.get(1)
            ?: error("Missing string key: $key")
    }
}
