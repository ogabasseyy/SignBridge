# SignBridge

SignBridge is an offline-first Android MVP for the Gemma 4 Good Hackathon, Digital Equity & Inclusivity track. It is scoped to one bounded communication workflow: helping a Deaf signer communicate with a hearing person in a stressful Lagos roadside interaction.

## Bounded Scope

This is not a universal sign-language translator. The hackathon MVP targets 25 fixed phrases, one demo scenario, and one bidirectional flow.

## Privacy Promise

No backend, accounts, analytics, or cloud sync are part of the hackathon build. Camera frames and microphone audio are processed on-device. Raw video and audio are not stored by default.

## Setup

The project is a single-module native Android app using Kotlin and Jetpack Compose.

## Architecture

CameraX and MediaPipe produce landmark windows, a LiteRT/TFLite classifier predicts phrase candidates, Gemma rewrites selected glosses into speakable text, and Android TextToSpeech speaks the result. The reverse loop uses on-device speech recognition plus Gemma condensation.

## Known Limitations

Gemma runtime availability must be verified on the target S24 Ultra before product work continues. The app intentionally supports a limited phrase set and documents signer-dependent evaluation.

