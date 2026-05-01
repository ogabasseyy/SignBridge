# Video Script

Target length: 2:45.

## 0:00-0:20

Lagos roadside establishing shot.

Caption: `When you're Deaf and the other person doesn't sign, a small conflict can become dangerous fast.`

## 0:20-0:45

Show minor collision setup. Hearing driver is frustrated. Deaf driver tries to sign but is misunderstood.

## 0:45-1:40

Show phone in airplane mode.

Open SignBridge. Use Sign to Speech or fallback phrase selection. Phone speaks:

`Please calm down. I am Deaf. It was an accident. My brakes failed.`

Show the hearing driver calming down.

## 1:40-2:10

Hearing driver replies. Use Listen mode or typed fallback depending on physical S24 speech status.

Show large readable text.

## 2:10-2:35

Architecture overlay:

CameraX -> MediaPipe landmarks -> TFLite classifier -> Gemma rewrite/tool trace -> TTS.

Reverse:

Speech/typed reply -> Gemma condensation -> large text.

## 2:35-2:45

Closing caption:

`SignBridge is not replacing interpreters. It is a bridge for moments when none are available, and it stays usable without a network.`

## Captioning

Captions must be present throughout.

## Honesty Rule

Only show live sign recognition, Gemma runtime, or microphone speech recognition if those paths pass physical S24 QA. Otherwise, label the fallback honestly in the narration/write-up.
