# VoiceCore

**Modern AI Voice Assistant for Android**

Dark futuristic UI • Voice & Text chat • Speech-to-Text • Text-to-Speech • Conversation history

---

## Features

- **Voice conversation** — tap the microphone, speak, get spoken replies
- **Text chat** — full messaging interface
- **Conversation history** — stored locally with Room
- **Real-time status** — Idle / Listening / Thinking / Speaking
- **Animated voice core** — visual feedback for every state
- **Configurable AI** — any OpenAI-compatible API (OpenAI, Groq, Together, local models via proxy, etc.)
- **Offline voice** — recognition and TTS work without internet; only AI replies need an API key

## Screenshots / UI

Dark graphite background, neon purple & cyan accents, glass-style surfaces, smooth animations.

## How to connect AI API

1. Open the app → **Settings**
2. Enter:
   - **Base URL** — e.g. `https://api.openai.com/v1` or `https://api.groq.com/openai/v1`
   - **API Key** — your key
   - **Model** — e.g. `gpt-4o-mini`, `llama-3.1-8b-instant`, etc.
   - **System Prompt** (optional)
3. Tap **Save**

After that, both voice and text modes will use the configured model.

## Build

### Via GitHub Actions (recommended)

Every push to `main` triggers a build. Go to the **Actions** tab → latest workflow run → download **VoiceCore-debug** artifact.

You can also trigger manually: Actions → Build APK → Run workflow.

### Locally

```bash
./gradlew assembleDebug
```

APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## Tech Stack

- Kotlin
- Jetpack Compose + Material 3
- MVVM + Coroutines + Flow
- Room (history)
- DataStore (settings)
- Android SpeechRecognizer + TextToSpeech
- OkHttp (API)

## Permissions

- `RECORD_AUDIO` — voice input
- `INTERNET` — AI API calls

---

Made for modern Android (minSdk 26).
