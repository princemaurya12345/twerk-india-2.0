package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChatSessionEntity
import com.example.data.model.ChatMessage
import com.example.data.model.Language
import com.example.data.model.MessageSender
import com.example.data.model.PartnerProfile
import com.example.data.model.ReactionEmoji
import com.example.data.model.SupportedLanguages
import com.example.data.repository.ChatHistoryRepository
import com.example.data.repository.MatchRepository
import com.example.data.service.TranslationService
import com.example.data.service.TtsManager
import com.example.ui.camera.VideoFilter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class ChatScreenState {
    LOBBY,
    MATCHING,
    CALL_RINGING,
    CONNECTED
}

enum class CallConnectMode(val label: String, val description: String, val icon: String) {
    INSTANT_CONNECT("Instant Connect", "Auto-connects immediately like Omegle", "⚡"),
    RING_AND_ACCEPT("Ring & Accept", "Ring first; accept or decline before connecting", "📞")
}

enum class CallType(val label: String, val icon: String) {
    VIDEO_CALL("Video Call", "📹"),
    AUDIO_FIRST("Audio Call", "🎙️"),
    TEXT_FIRST("Text First", "💬")
}

enum class MatchingVibe(val label: String, val description: String) {
    FAST_RANDOM("All India", "Match with any online desi (Fastest)"),
    CROSS_CULTURAL("Translation Exchange", "Match different language for live translation"),
    SAME_LANGUAGE("Same Language", "Match speakers of your native tongue")
}

enum class QualityMode(val label: String) {
    HD_720P("HD 720p"),
    DATA_SAVER("Data Saver (4G)")
}

data class LiveSubtitle(
    val speaker: String,
    val originalText: String,
    val translatedText: String,
    val sourceLangCode: String,
    val targetLangCode: String,
    val provider: String = "Open Source API"
)

data class VideoChatUiState(
    val screenState: ChatScreenState = ChatScreenState.LOBBY,
    val myCodename: String = "DesiNomad #82",
    val myNativeLang: Language = SupportedLanguages.getByCode("en"),
    val targetTranslationLang: Language = SupportedLanguages.getByCode("hi"),
    val selectedStateFilter: String = "All India",
    val selectedInterests: List<String> = listOf("#Bollywood", "#CricketIPL", "#Chai"),
    val safeModeEnabled: Boolean = true,
    // Call Connection Options
    val callConnectMode: CallConnectMode = CallConnectMode.INSTANT_CONNECT,
    val callType: CallType = CallType.VIDEO_CALL,
    val matchingVibe: MatchingVibe = MatchingVibe.FAST_RANDOM,
    val qualityMode: QualityMode = QualityMode.HD_720P,
    val isLocalCameraEnabled: Boolean = true,
    val isFrontCamera: Boolean = true,
    val isMicMuted: Boolean = false,
    val activeFilter: VideoFilter = VideoFilter.NORMAL,
    val partner: PartnerProfile? = null,
    val isPartnerBlurred: Boolean = false,
    val activeSubtitle: LiveSubtitle? = null,
    val showDualSubtitles: Boolean = true,
    val autoSpeakTranslations: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val isTypingResponse: Boolean = false,
    val reactions: List<ReactionEmoji> = emptyList(),
    val matchingTimeSeconds: Int = 0,
    val totalMatchesCount: Int = 14,
    // Room Database History
    val chatHistory: List<ChatSessionEntity> = emptyList(),
    // Firebase Cloud Sync & Auth
    val userEmail: String? = null,
    val userDisplayName: String? = null,
    val isCloudSyncEnabled: Boolean = false,
    // Sandbox test field
    val sandboxInput: String = "Where is the best street food in India?",
    val sandboxOutput: String = "",
    val isSandboxTranslating: Boolean = false
)

class VideoChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(VideoChatUiState())
    val uiState: StateFlow<VideoChatUiState> = _uiState.asStateFlow()

    val authManager = com.example.ui.auth.AuthManager(application)
    private val firestoreSync = com.example.data.repository.FirestoreSyncRepository(application)
    private val matchRepository = MatchRepository()
    private val translationService = TranslationService()
    private val ttsManager = TtsManager(application)
    private val database = AppDatabase.getInstance(application)
    private val historyRepository = ChatHistoryRepository(database.chatSessionDao())

    private var partnerSimJob: Job? = null
    private var matchingJob: Job? = null
    private var callStartTime: Long = 0L

    init {
        randomizeCodename()
        observeHistory()
        observeAuth()
        authManager.attemptAutoSignIn(viewModelScope)
    }

    private fun observeAuth() {
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                _uiState.update {
                    it.copy(
                        userEmail = user?.email,
                        userDisplayName = user?.displayName,
                        isCloudSyncEnabled = user != null
                    )
                }
                if (user != null) {
                    firestoreSync.syncUserProfile(
                        codename = _uiState.value.myCodename,
                        nativeLang = _uiState.value.myNativeLang.code,
                        targetLang = _uiState.value.targetTranslationLang.code,
                        state = _uiState.value.selectedStateFilter,
                        totalMatches = _uiState.value.totalMatchesCount
                    )
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            historyRepository.allSessions.collect { list ->
                _uiState.update { it.copy(chatHistory = list) }
            }
        }
    }

    fun randomizeCodename() {
        val prefixes = listOf("DesiSwag", "MumbaiMirchi", "DilliDilwala", "PunjabDaSher", "ChaiLover", "FilterKaapi", "BengalTiger", "BiryaniBoss", "TechDesi", "GamerBhai")
        val number = Random.nextInt(10, 999)
        _uiState.update { it.copy(myCodename = "${prefixes.random()} #$number") }
    }

    fun setMyNativeLanguage(lang: Language) {
        _uiState.update { it.copy(myNativeLang = lang) }
    }

    fun setTargetTranslationLanguage(lang: Language) {
        _uiState.update { it.copy(targetTranslationLang = lang) }
    }

    fun setStateFilter(state: String) {
        _uiState.update { it.copy(selectedStateFilter = state) }
    }

    fun toggleInterest(interest: String) {
        _uiState.update { current ->
            val list = current.selectedInterests.toMutableList()
            if (list.contains(interest)) {
                list.remove(interest)
            } else {
                list.add(interest)
            }
            current.copy(selectedInterests = list)
        }
    }

    fun toggleSafeMode() {
        _uiState.update { it.copy(safeModeEnabled = !it.safeModeEnabled) }
    }

    fun toggleLocalCamera() {
        _uiState.update { it.copy(isLocalCameraEnabled = !it.isLocalCameraEnabled) }
    }

    fun switchCameraLens() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun toggleMute() {
        _uiState.update { it.copy(isMicMuted = !it.isMicMuted) }
    }

    fun setVideoFilter(filter: VideoFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
    }

    fun togglePartnerBlur() {
        _uiState.update { it.copy(isPartnerBlurred = !it.isPartnerBlurred) }
    }

    fun toggleDualSubtitles() {
        _uiState.update { it.copy(showDualSubtitles = !it.showDualSubtitles) }
    }

    fun toggleAutoSpeak() {
        _uiState.update { it.copy(autoSpeakTranslations = !it.autoSpeakTranslations) }
    }

    fun speakCurrentSubtitle() {
        val sub = _uiState.value.activeSubtitle ?: return
        ttsManager.speak(sub.translatedText, sub.targetLangCode)
    }

    fun setCallConnectMode(mode: CallConnectMode) {
        _uiState.update { it.copy(callConnectMode = mode) }
    }

    fun setCallType(type: CallType) {
        _uiState.update {
            it.copy(
                callType = type,
                isLocalCameraEnabled = type != CallType.AUDIO_FIRST
            )
        }
    }

    fun setMatchingVibe(vibe: MatchingVibe) {
        _uiState.update { it.copy(matchingVibe = vibe) }
    }

    fun setQualityMode(mode: QualityMode) {
        _uiState.update { it.copy(qualityMode = mode) }
    }

    fun startMatching() {
        matchingJob?.cancel()
        partnerSimJob?.cancel()
        _uiState.update {
            it.copy(
                screenState = ChatScreenState.MATCHING,
                messages = emptyList(),
                activeSubtitle = null,
                partner = null,
                matchingTimeSeconds = 0
            )
        }

        matchingJob = viewModelScope.launch {
            // Realistic search latency: 1-2.5 seconds
            val delayDuration = Random.nextLong(1200, 2400)
            delay(delayDuration)

            // Select partner based on matching vibe & state
            val stateFilter = when (_uiState.value.matchingVibe) {
                MatchingVibe.FAST_RANDOM -> _uiState.value.selectedStateFilter
                MatchingVibe.SAME_LANGUAGE -> _uiState.value.selectedStateFilter
                MatchingVibe.CROSS_CULTURAL -> "All India"
            }
            val partner = matchRepository.getRandomPartner(stateFilter)

            if (_uiState.value.callConnectMode == CallConnectMode.RING_AND_ACCEPT) {
                // Ringing screen where user confirms to connect
                _uiState.update {
                    it.copy(
                        screenState = ChatScreenState.CALL_RINGING,
                        partner = partner,
                        totalMatchesCount = it.totalMatchesCount + 1
                    )
                }
            } else {
                // Instant connect
                callStartTime = System.currentTimeMillis()
                _uiState.update {
                    it.copy(
                        screenState = ChatScreenState.CONNECTED,
                        partner = partner,
                        totalMatchesCount = it.totalMatchesCount + 1,
                        isPartnerBlurred = it.safeModeEnabled,
                        isLocalCameraEnabled = it.callType != CallType.AUDIO_FIRST
                    )
                }
                simulatePartnerGreeting(partner)
            }
        }
    }

    fun acceptIncomingCall() {
        val partner = _uiState.value.partner ?: return
        callStartTime = System.currentTimeMillis()
        _uiState.update {
            it.copy(
                screenState = ChatScreenState.CONNECTED,
                isPartnerBlurred = it.safeModeEnabled,
                isLocalCameraEnabled = it.callType != CallType.AUDIO_FIRST
            )
        }
        simulatePartnerGreeting(partner)
    }

    fun declineIncomingCall() {
        // Skip to next match immediately
        startMatching()
    }

    fun nextMatch() {
        saveCurrentSessionIfActive()
        startMatching()
    }

    fun disconnectToLobby() {
        saveCurrentSessionIfActive()
        matchingJob?.cancel()
        partnerSimJob?.cancel()
        _uiState.update {
            it.copy(
                screenState = ChatScreenState.LOBBY,
                partner = null,
                activeSubtitle = null,
                messages = emptyList()
            )
        }
    }

    private fun saveCurrentSessionIfActive() {
        val currentPartner = _uiState.value.partner ?: return
        val duration = if (callStartTime > 0L) {
            ((System.currentTimeMillis() - callStartTime) / 1000).coerceAtLeast(1)
        } else 4L
        val msgs = _uiState.value.messages
        val lastMsg = msgs.lastOrNull()

        val session = ChatSessionEntity(
            partnerCodename = currentPartner.codename,
            partnerLocation = currentPartner.location,
            partnerState = currentPartner.state,
            partnerLanguage = currentPartner.nativeLanguage,
            partnerInterests = currentPartner.interests.joinToString(", "),
            callType = _uiState.value.callType.label,
            durationSeconds = duration,
            messageCount = msgs.size,
            lastMessageExcerpt = lastMsg?.originalText ?: "Live video match",
            lastTranslationExcerpt = lastMsg?.translatedText ?: "",
            avatarGradientStart = currentPartner.avatarGradientStart,
            avatarGradientEnd = currentPartner.avatarGradientEnd
        )

        viewModelScope.launch {
            historyRepository.saveSession(session)
            firestoreSync.backupSessionToCloud(session)
        }
        callStartTime = 0L
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteSession(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }

    fun sendUserMessage(text: String) {
        if (text.isBlank()) return
        val currentPartner = _uiState.value.partner ?: return
        val myLang = _uiState.value.myNativeLang.code
        val partnerLang = currentPartner.nativeLanguage

        val userMessage = ChatMessage(
            sender = MessageSender.YOU,
            senderName = _uiState.value.myCodename,
            originalText = text.trim(),
            sourceLang = myLang,
            targetLang = partnerLang,
            isTranslating = true
        )

        _uiState.update { it.copy(messages = it.messages + userMessage) }

        viewModelScope.launch {
            // Translate user's message to partner's language
            val translation = translationService.translate(
                text = text.trim(),
                sourceLang = myLang,
                targetLang = partnerLang
            )

            _uiState.update { state ->
                val updated = state.messages.map { msg ->
                    if (msg.id == userMessage.id) {
                        msg.copy(
                            translatedText = translation.translatedText,
                            isTranslating = false
                        )
                    } else msg
                }
                state.copy(
                    messages = updated,
                    activeSubtitle = LiveSubtitle(
                        speaker = "You",
                        originalText = text.trim(),
                        translatedText = translation.translatedText,
                        sourceLangCode = myLang,
                        targetLangCode = partnerLang,
                        provider = translation.provider
                    )
                )
            }

            // Simulate partner typing & response
            schedulePartnerResponse(currentPartner)
        }
    }

    fun sendIcebreaker() {
        val icebreaker = matchRepository.getRandomIcebreaker()
        sendUserMessage(icebreaker)
    }

    private fun simulatePartnerGreeting(partner: PartnerProfile) {
        partnerSimJob?.cancel()
        partnerSimJob = viewModelScope.launch {
            delay(1000)
            val greeting = matchRepository.getInitialGreeting(partner)
            processPartnerSpeech(partner, greeting)
        }
    }

    private fun schedulePartnerResponse(partner: PartnerProfile) {
        partnerSimJob?.cancel()
        partnerSimJob = viewModelScope.launch {
            _uiState.update { it.copy(isTypingResponse = true) }
            delay(Random.nextLong(1500, 3000))
            _uiState.update { it.copy(isTypingResponse = false) }

            val response = matchRepository.getPartnerResponse(partner.nativeLanguage)
            processPartnerSpeech(partner, response)
        }
    }

    private suspend fun processPartnerSpeech(partner: PartnerProfile, originalSpeech: String) {
        val myTargetLang = _uiState.value.targetTranslationLang.code

        val translation = translationService.translate(
            text = originalSpeech,
            sourceLang = partner.nativeLanguage,
            targetLang = myTargetLang
        )

        val partnerMsg = ChatMessage(
            sender = MessageSender.STRANGER,
            senderName = partner.codename,
            originalText = originalSpeech,
            translatedText = translation.translatedText,
            sourceLang = partner.nativeLanguage,
            targetLang = myTargetLang
        )

        val subtitle = LiveSubtitle(
            speaker = partner.codename,
            originalText = originalSpeech,
            translatedText = translation.translatedText,
            sourceLangCode = partner.nativeLanguage,
            targetLangCode = myTargetLang,
            provider = translation.provider
        )

        _uiState.update {
            it.copy(
                messages = it.messages + partnerMsg,
                activeSubtitle = subtitle
            )
        }

        if (_uiState.value.autoSpeakTranslations) {
            ttsManager.speak(translation.translatedText, myTargetLang)
        }
    }

    fun triggerReaction(emoji: String) {
        val newReaction = ReactionEmoji(
            emoji = emoji,
            startXFraction = Random.nextFloat() * 0.7f + 0.15f
        )
        _uiState.update { it.copy(reactions = it.reactions + newReaction) }

        viewModelScope.launch {
            delay(2800)
            _uiState.update { state ->
                state.copy(reactions = state.reactions.filter { it.id != newReaction.id })
            }
        }
    }

    fun testSandboxTranslation() {
        val input = _uiState.value.sandboxInput
        if (input.isBlank()) return
        _uiState.update { it.copy(isSandboxTranslating = true) }

        viewModelScope.launch {
            val result = translationService.translate(
                text = input,
                sourceLang = _uiState.value.myNativeLang.code,
                targetLang = _uiState.value.targetTranslationLang.code
            )
            _uiState.update {
                it.copy(
                    sandboxOutput = "${result.translatedText} (${result.provider})",
                    isSandboxTranslating = false
                )
            }
        }
    }

    fun updateSandboxInput(newText: String) {
        _uiState.update { it.copy(sandboxInput = newText) }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.destroy()
    }
}
