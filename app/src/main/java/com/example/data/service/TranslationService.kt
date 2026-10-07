package com.example.data.service

import android.util.Log
import com.example.data.model.TranslationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

class TranslationService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // Common phrase cache for instant low-latency translations
    private val localPhrasebook: Map<String, Map<String, String>> = mapOf(
        "hello" to mapOf("hi" to "नमस्ते", "bn" to "হ্যালো", "ta" to "வணக்கம்", "te" to "నమస్కారం", "mr" to "नमस्कार", "gu" to "નમસ્તે", "pa" to "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ", "ur" to "ہیلو"),
        "namaste" to mapOf("en" to "Hello! Greetings!", "es" to "¡Hola! Saludos", "fr" to "Bonjour!"),
        "how are you?" to mapOf("hi" to "आप कैसे हैं?", "bn" to "আপনি কেমন আছেন?", "ta" to "நீங்கள் எப்படி இருக்கிறீர்கள்?", "te" to "మీరు ఎలా ఉన్నారు?", "mr" to "तुम्ही कसे आहात?", "gu" to "તમે કેમ છો?"),
        "aap kaise ho?" to mapOf("en" to "How are you doing?", "ta" to "எப்படி இருக்கீங்க?"),
        "where are you from?" to mapOf("hi" to "आप कहाँ से हैं?", "bn" to "আপনি কোথা থেকে এসেছেন?", "ta" to "நீங்கள் எங்கிருந்து வருகிறீர்கள்?", "te" to "మీరు ఎక్కడ నుండి వచ్చారు?", "mr" to "तुम्ही कुठून आहात?"),
        "nice to meet you" to mapOf("hi" to "आपसे मिलकर अच्छा लगा", "bn" to "আপনার সাথে দেখা করে ভালো লাগলো", "ta" to "உங்களை சந்தித்ததில் மகிழ்ச்சி", "te" to "మిమ్మల్ని కలవడం సంతోషంగా ఉంది"),
        "what do you do?" to mapOf("hi" to "आप क्या करते हैं?", "bn" to "আপনি কি করেন?", "ta" to "நீங்கள் என்ன வேலை செய்கிறீர்கள்?"),
        "what are your hobbies?" to mapOf("hi" to "आपके शौक क्या हैं?", "bn" to "আপনার শখ কি?", "ta" to "உங்கள் பொழுதுபோக்குகள் என்ன?"),
        "do you like bollywood?" to mapOf("hi" to "क्या आपको बॉलीवुड पसंद है?", "bn" to "আপনি কি বলিউড পছন্দ করেন?", "ta" to "பாலிவுட் பிடிக்குமா?")
    )

    suspend fun translate(
        text: String,
        sourceLang: String = "auto",
        targetLang: String = "hi"
    ): TranslationResult = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return@withContext TranslationResult(text, "", sourceLang, targetLang, "Empty", false)
        }

        if (sourceLang.equals(targetLang, ignoreCase = true)) {
            return@withContext TranslationResult(text, text, sourceLang, targetLang, "Identical", true)
        }

        // Check local quick phrasebook for instant match
        val normalized = trimmed.lowercase().removeSuffix("?").trim()
        val quickMatch = localPhrasebook[normalized]?.get(targetLang)
            ?: localPhrasebook[trimmed.lowercase()]?.get(targetLang)
        if (quickMatch != null) {
            return@withContext TranslationResult(
                originalText = trimmed,
                translatedText = quickMatch,
                sourceLang = sourceLang,
                targetLang = targetLang,
                provider = "Local Cache (Instant)",
                isSuccess = true
            )
        }

        // 1. Try LibreTranslate Open Source API
        tryLibreTranslate(trimmed, sourceLang, targetLang)?.let { return@withContext it }

        // 2. Try MyMemory Open Source Translation API
        tryMyMemory(trimmed, sourceLang, targetLang)?.let { return@withContext it }

        // 3. Try Lingva Open Source API
        tryLingva(trimmed, sourceLang, targetLang)?.let { return@withContext it }

        // Fallback: Return original text with notice if offline/unreachable
        TranslationResult(
            originalText = trimmed,
            translatedText = trimmed,
            sourceLang = sourceLang,
            targetLang = targetLang,
            provider = "Offline Fallback",
            isSuccess = false
        )
    }

    private fun tryLibreTranslate(text: String, src: String, tgt: String): TranslationResult? {
        val mirrors = listOf(
            "https://translate.terraprint.co/translate",
            "https://libretranslate.de/translate"
        )
        val s = if (src == "auto") "auto" else src

        for (endpoint in mirrors) {
            try {
                val jsonBody = JSONObject().apply {
                    put("q", text)
                    put("source", s)
                    put("target", tgt)
                    put("format", "text")
                }
                val request = Request.Builder()
                    .url(endpoint)
                    .post(jsonBody.toString().toRequestBody(jsonMediaType))
                    .header("User-Agent", "TwerkIndia-App/1.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: return@use
                        val json = JSONObject(body)
                        if (json.has("translatedText")) {
                            val translated = json.getString("translatedText")
                            if (translated.isNotBlank()) {
                                return TranslationResult(
                                    originalText = text,
                                    translatedText = translated,
                                    sourceLang = src,
                                    targetLang = tgt,
                                    provider = "LibreTranslate (Open-Source)",
                                    isSuccess = true
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("TranslationService", "LibreTranslate error on $endpoint: ${e.message}")
            }
        }
        return null
    }

    private fun tryMyMemory(text: String, src: String, tgt: String): TranslationResult? {
        try {
            val s = if (src == "auto") "en" else src
            val encodedQuery = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
            val langpair = "$s|$tgt"
            val url = "https://api.mymemory.translated.net/get?q=$encodedQuery&langpair=$langpair"

            val request = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "TwerkIndia-App/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val json = JSONObject(body)
                    val responseData = json.optJSONObject("responseData")
                    if (responseData != null) {
                        val translated = responseData.optString("translatedText")
                        if (translated.isNotBlank() && !translated.startsWith("MYMEMORY WARNING")) {
                            return TranslationResult(
                                originalText = text,
                                translatedText = translated,
                                sourceLang = s,
                                targetLang = tgt,
                                provider = "MyMemory (Open API)",
                                isSuccess = true
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("TranslationService", "MyMemory error: ${e.message}")
        }
        return null
    }

    private fun tryLingva(text: String, src: String, tgt: String): TranslationResult? {
        try {
            val s = if (src == "auto") "auto" else src
            val encodedQuery = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
            val url = "https://lingva.ml/api/v1/$s/$tgt/$encodedQuery"

            val request = Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", "TwerkIndia-App/1.0")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return null
                    val json = JSONObject(body)
                    val translation = json.optString("translation")
                    if (translation.isNotBlank()) {
                        return TranslationResult(
                            originalText = text,
                            translatedText = translation,
                            sourceLang = s,
                            targetLang = tgt,
                            provider = "Lingva (Open Source)",
                            isSuccess = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            Log.w("TranslationService", "Lingva error: ${e.message}")
        }
        return null
    }
}
