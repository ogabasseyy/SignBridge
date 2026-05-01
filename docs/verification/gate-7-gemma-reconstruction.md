# Gate 7 Gemma Behavior Review

Date: 2026-05-01

## Result

Partially passed.

Prompt construction, app-level tool trace, constrained JSON parsing, deterministic fallback, runtime decision policy, and client interfaces are implemented. Live Gemma generation is still blocked on physical S24 Ultra Gate 0 verification.

## Review

- Prompt enforces:
  - no added facts
  - Nigerian English
  - JSON schema
  - visible tool trace
- Tool tests cover:
  - context detection
  - tone selection
  - reply intent extraction
- Translation JSON parser handles:
  - valid JSON
  - malformed output fallback
  - missing field fallback
  - `needs_confirmation`
- Runtime selector follows Gate 0:
  - ML Kit Prompt API if a probe satisfies both rewrite and condensation checks
  - LiteRT-LM if ML Kit is unavailable but LiteRT is available
  - hard stop if neither works
- LiteRT config carries model path, backend, max output tokens, and offline-only flag.
- `docs/verification/gemma-tooling-claim.md` explicitly avoids overclaiming native function calling.

## Remaining Gate Work

- Complete physical S24 Ultra Gate 0.
- Replace deterministic `MlKitGemmaClient.generate(...)` fallback with live Prompt API generation only after runtime availability is verified.
- Enable and implement `LiteRtGemmaClient` against LiteRT-LM only if Prompt API fails on the S24 Ultra.
- Run at least 20 no-added-facts prompt cases against the selected live runtime.

## Follow-Up Wiring Completed

- Selected prediction -> app-level tool trace -> `GemmaClient.reconstructSentence(...)` -> speakable preview -> manual TTS action is wired in the Sign to Speech screen.
- Evidence: `docs/verification/test-android-apps-smoke-forward-translation.md`

## Verification Commands

- `./gradlew testDebugUnitTest --tests com.signbridge.tools.SignBridgeToolsTest --tests com.signbridge.gemma.GemmaPromptBuilderTest --tests com.signbridge.gemma.TranslationJsonTest --tests com.signbridge.gemma.GemmaModelSelectionTest --tests com.signbridge.gemma.GemmaRuntimeSelectorTest --tests com.signbridge.gemma.LiteRtGemmaClientConfigTest`
