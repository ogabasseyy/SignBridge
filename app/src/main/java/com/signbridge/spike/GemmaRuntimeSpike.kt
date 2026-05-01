package com.signbridge.spike

import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.Generation
import com.google.mlkit.genai.prompt.ModelPreference
import com.google.mlkit.genai.prompt.ModelReleaseStage
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import com.google.mlkit.genai.prompt.generationConfig
import com.google.mlkit.genai.prompt.modelConfig
import com.google.mlkit.genai.speechrecognition.SpeechRecognition
import com.google.mlkit.genai.speechrecognition.SpeechRecognizerOptions
import com.google.mlkit.genai.speechrecognition.speechRecognizerOptions
import com.signbridge.ml.ApiRealityGate
import com.signbridge.ml.PromptRuntime
import com.signbridge.ml.PromptRuntimeProbe
import com.signbridge.ml.PromptTaskResult
import com.signbridge.ml.RuntimeStatus
import com.signbridge.ml.SpeechRuntimeMode
import com.signbridge.ml.SpeechRuntimeProbe
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.system.measureTimeMillis

class GemmaRuntimeSpike {
    suspend fun runAll(): ApiRealityGate = withContext(Dispatchers.IO) {
        val promptProbes = PromptRuntime.entries.map { runtime ->
            probePromptRuntime(runtime)
        }
        val speechProbes = SpeechRuntimeMode.entries.map { mode ->
            probeSpeechRuntime(mode)
        }
        ApiRealityGate(
            promptProbes = promptProbes,
            speechProbes = speechProbes,
        )
    }

    private suspend fun probePromptRuntime(runtime: PromptRuntime): PromptRuntimeProbe =
        withTimeoutOrNull(PROMPT_PROBE_TIMEOUT_MILLIS) {
            probePromptRuntimeWithoutTimeout(runtime)
        } ?: PromptRuntimeProbe(
            runtime = runtime,
            status = RuntimeStatus.Error("timed out after ${PROMPT_PROBE_TIMEOUT_MILLIS}ms"),
        )

    private suspend fun probePromptRuntimeWithoutTimeout(runtime: PromptRuntime): PromptRuntimeProbe {
        val model = when (runtime) {
            PromptRuntime.PreviewFull -> Generation.getClient(
                generationConfig {
                    modelConfig = modelConfig {
                        releaseStage = ModelReleaseStage.PREVIEW
                        preference = ModelPreference.FULL
                    }
                },
            )

            PromptRuntime.PreviewFast -> Generation.getClient(
                generationConfig {
                    modelConfig = modelConfig {
                        releaseStage = ModelReleaseStage.PREVIEW
                        preference = ModelPreference.FAST
                    }
                },
            )

            PromptRuntime.Stable -> Generation.getClient()
        }

        return try {
            val status = model.checkStatus().toRuntimeStatus()
            if (status != RuntimeStatus.Available) {
                PromptRuntimeProbe(runtime = runtime, status = status)
            } else {
                model.warmup()
                PromptRuntimeProbe(
                    runtime = runtime,
                    status = status,
                    rewrite = model.generateMeasured(SIGN_REWRITE_PROMPT),
                    condensation = model.generateMeasured(REPLY_CONDENSE_PROMPT),
                )
            }
        } catch (throwable: Throwable) {
            PromptRuntimeProbe(
                runtime = runtime,
                status = RuntimeStatus.Error(throwable.readableMessage()),
            )
        } finally {
            model.close()
        }
    }

    private suspend fun probeSpeechRuntime(mode: SpeechRuntimeMode): SpeechRuntimeProbe =
        withTimeoutOrNull(SPEECH_PROBE_TIMEOUT_MILLIS) {
            probeSpeechRuntimeWithoutTimeout(mode)
        } ?: SpeechRuntimeProbe(
            mode = mode,
            status = RuntimeStatus.Error("timed out after ${SPEECH_PROBE_TIMEOUT_MILLIS}ms"),
        )

    private suspend fun probeSpeechRuntimeWithoutTimeout(mode: SpeechRuntimeMode): SpeechRuntimeProbe {
        val recognizer = SpeechRecognition.getClient(
            speechRecognizerOptions {
                locale = Locale.US
                preferredMode = when (mode) {
                    SpeechRuntimeMode.Basic -> SpeechRecognizerOptions.Mode.MODE_BASIC
                    SpeechRuntimeMode.Advanced -> SpeechRecognizerOptions.Mode.MODE_ADVANCED
                }
            },
        )

        return try {
            SpeechRuntimeProbe(
                mode = mode,
                status = recognizer.checkStatus().toRuntimeStatus(),
            )
        } catch (throwable: Throwable) {
            SpeechRuntimeProbe(
                mode = mode,
                status = RuntimeStatus.Error(throwable.readableMessage()),
            )
        } finally {
            recognizer.close()
        }
    }

    private suspend fun com.google.mlkit.genai.prompt.GenerativeModel.generateMeasured(
        prompt: String,
    ): PromptTaskResult {
        var text = ""
        val latency = measureTimeMillis {
            val response = generateContent(
                generateContentRequest(TextPart(prompt)) {
                    temperature = 0.0f
                    topK = 1
                    candidateCount = 1
                    maxOutputTokens = 96
                },
            )
            text = response.candidates.firstOrNull()?.text.orEmpty()
        }
        return PromptTaskResult(text = text, totalLatencyMillis = latency)
    }

    private fun Int.toRuntimeStatus(): RuntimeStatus =
        when (this) {
            FeatureStatus.AVAILABLE -> RuntimeStatus.Available
            FeatureStatus.DOWNLOADABLE -> RuntimeStatus.Downloadable
            FeatureStatus.DOWNLOADING -> RuntimeStatus.Downloading
            FeatureStatus.UNAVAILABLE -> RuntimeStatus.Unavailable
            else -> RuntimeStatus.Error("unknown status code $this")
        }

    private fun Throwable.readableMessage(): String =
        message ?: javaClass.simpleName

    private companion object {
        private const val PROMPT_PROBE_TIMEOUT_MILLIS = 20_000L
        private const val SPEECH_PROBE_TIMEOUT_MILLIS = 8_000L

        private const val SIGN_REWRITE_PROMPT =
            "You are SignBridge. Convert these recognized sign glosses into one calm spoken Nigerian English sentence. " +
                "Do not add facts. Glosses: PLEASE CALM DOWN, I AM DEAF, IT WAS AN ACCIDENT, MY BRAKES FAILED."

        private const val REPLY_CONDENSE_PROMPT =
            "Condense this hearing person's reply into one sentence the Deaf user can read quickly. " +
                "Preserve key facts and do not add information. Reply: Okay, I understand now. Move your car to the side and show me your insurance."
    }
}
