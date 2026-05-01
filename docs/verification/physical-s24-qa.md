# Physical S24 Ultra QA

Date: 2026-05-01

## Status

Pending. This workspace does not currently have the Galaxy S24 Ultra attached, so physical-device claims are intentionally not marked as passed.

## Required Device Details To Record

- Device model:
- Android version:
- One UI version:
- AICore version:
- Google Play services version:
- AI Edge Gallery version:
- AICore Developer Preview enrollment:
- Bootloader locked:

## Required Checks

1. Install Google AI Edge Gallery.
2. Download Gemma 4 E2B and E4B in AI Edge Gallery.
3. Run one online prompt.
4. Enable airplane mode.
5. Run one offline prompt.
6. Install SignBridge debug/release APK.
7. Run the app from a cold launch in airplane mode.
8. Record ML Kit Prompt API status for Preview FULL, Preview FAST, and Stable.
9. Record ML Kit Speech Recognition Basic and Advanced status.
10. Run Emergency grid.
11. Run Sign to Speech camera preview.
12. Run a real phrase capture.
13. Run Listen mode or typed fallback.
14. Repeat the exact demo path three times.

## Current Decision

Physical S24 QA is a release blocker for any public claim that SignBridge runs Gemma 4 E4B offline in-app. Until this file is completed, README and WRITEUP must say that live Gemma runtime validation is pending.
