package com.example.data.model

data class Language(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val ttsIso: String = code
)

object SupportedLanguages {
    val ALL = listOf(
        Language("hi", "Hindi", "हिन्दी", "🇮🇳", "hi"),
        Language("en", "English", "English", "🌐", "en"),
        Language("bn", "Bengali", "বাংলা", "🇮🇳", "bn"),
        Language("te", "Telugu", "తెలుగు", "🇮🇳", "te"),
        Language("mr", "Marathi", "मराठी", "🇮🇳", "mr"),
        Language("ta", "Tamil", "தமிழ்", "🇮🇳", "ta"),
        Language("gu", "Gujarati", "ગુજરાતી", "🇮🇳", "gu"),
        Language("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳", "kn"),
        Language("ml", "Malayalam", "മലയാളം", "🇮🇳", "ml"),
        Language("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳", "pa"),
        Language("ur", "Urdu", "اردو", "🇮🇳", "ur"),
        Language("or", "Odia", "ଓଡ଼ିଆ", "🇮🇳", "or"),
        Language("es", "Spanish", "Español", "🇪🇸", "es"),
        Language("fr", "French", "Français", "🇫🇷", "fr"),
        Language("ja", "Japanese", "日本語", "🇯🇵", "ja"),
        Language("de", "German", "Deutsch", "🇩🇪", "de")
    )

    fun getByCode(code: String): Language {
        return ALL.firstOrNull { it.code.equals(code, ignoreCase = true) }
            ?: Language("en", "English", "English", "🌐")
    }
}

data class TranslationResult(
    val originalText: String,
    val translatedText: String,
    val sourceLang: String,
    val targetLang: String,
    val provider: String,
    val isSuccess: Boolean = true
)

enum class MessageSender {
    YOU,
    STRANGER,
    SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val senderName: String,
    val originalText: String,
    val translatedText: String = "",
    val sourceLang: String = "en",
    val targetLang: String = "hi",
    val timestamp: Long = System.currentTimeMillis(),
    val isTranslating: Boolean = false
)

data class PartnerProfile(
    val id: String,
    val codename: String,
    val location: String,
    val state: String,
    val nativeLanguage: String,
    val interests: List<String>,
    val avatarGradientStart: Long,
    val avatarGradientEnd: Long,
    val bio: String,
    val verifiedDesi: Boolean = true
)

data class ReactionEmoji(
    val id: String = java.util.UUID.randomUUID().toString(),
    val emoji: String,
    val startXFraction: Float,
    val timestamp: Long = System.currentTimeMillis()
)
