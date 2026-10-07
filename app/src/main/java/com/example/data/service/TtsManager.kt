package com.example.data.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("hi", "IN")
            isReady = true
        } else {
            Log.e("TtsManager", "TTS initialization failed status: $status")
        }
    }

    fun speak(text: String, langCode: String = "hi") {
        if (!isReady || tts == null || text.isBlank()) return

        val locale = when (langCode.lowercase()) {
            "hi" -> Locale("hi", "IN")
            "bn" -> Locale("bn", "IN")
            "ta" -> Locale("ta", "IN")
            "te" -> Locale("te", "IN")
            "mr" -> Locale("mr", "IN")
            "gu" -> Locale("gu", "IN")
            "pa" -> Locale("pa", "IN")
            "ur" -> Locale("ur", "PK")
            "es" -> Locale("es", "ES")
            "fr" -> Locale.FRENCH
            "ja" -> Locale.JAPANESE
            "de" -> Locale.GERMAN
            else -> Locale.ENGLISH
        }

        try {
            tts?.language = locale
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TwerkIndia_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.w("TtsManager", "TTS speak failed: ${e.message}")
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun destroy() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isReady = false
        } catch (e: Exception) {
            Log.w("TtsManager", "Error shutting down TTS: ${e.message}")
        }
    }
}
