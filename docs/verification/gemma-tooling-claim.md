# Gemma Tooling Claim

Date: 2026-05-01

## Current Claim

SignBridge currently implements app-level tool orchestration around Gemma prompts.

Do not claim native Gemma function calling yet.

## Evidence

- `SignBridgeTools.detectContext(...)`
- `SignBridgeTools.selectTone(...)`
- `SignBridgeTools.extractIntent(...)`
- `GemmaPromptBuilder.reconstructionPrompt(...)` includes a visible tool trace:
  - `detect_context=...`
  - `select_tone=...`
- `GemmaRuntimeSelector` models the Gate 0 decision:
  - ML Kit Prompt API when verified
  - LiteRT-LM fallback when verified
  - hard stop when neither runtime works

## Native Function Calling Status

Not verified.

ML Kit Prompt API availability is still pending physical S24 Ultra Gate 0 verification. No native function-calling API has been proven in-app, so README, write-up, and video language must say "app-level tool orchestration" unless a later physical-device test proves native tool calls.
