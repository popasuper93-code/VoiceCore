package com.voicecore.app.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsHelper(context: Context) {

    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        tts = TextToSpeech(context) { status ->
            isReady = status == TextToSpeech.SUCCESS
            if (isReady) {
                tts?.language = Locale.getDefault()
                tts?.setSpeechRate(1.0f)
            }
        }
    }

    fun speak(text: String, onStart: () -> Unit = {}, onDone: () -> Unit = {}, onError: () -> Unit = {}) {
        if (!isReady || tts == null) {
            onError()
            return
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = onStart()
            override fun onDone(utteranceId: String?) = onDone()
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) = onError()
        })

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "voicecore_utterance")
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
