package com.example

import com.example.data.model.SupportedLanguages
import com.example.data.repository.MatchRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TranslationTest {

    @Test
    fun supportedLanguages_containsMajorIndianLanguages() {
        val hindi = SupportedLanguages.getByCode("hi")
        assertEquals("Hindi", hindi.displayName)
        assertEquals("हिन्दी", hindi.nativeName)

        val bengali = SupportedLanguages.getByCode("bn")
        assertEquals("Bengali", bengali.displayName)

        val tamil = SupportedLanguages.getByCode("ta")
        assertEquals("Tamil", tamil.displayName)

        val telugu = SupportedLanguages.getByCode("te")
        assertEquals("Telugu", telugu.displayName)
    }

    @Test
    fun matchRepository_providesRandomPartnerAndGreetings() {
        val repo = MatchRepository()
        val partner = repo.getRandomPartner()
        assertNotNull(partner)
        assertTrue(partner.codename.isNotBlank())
        assertTrue(partner.location.isNotBlank())

        val greeting = repo.getInitialGreeting(partner)
        assertTrue(greeting.isNotBlank())

        val icebreaker = repo.getRandomIcebreaker()
        assertTrue(icebreaker.isNotBlank())
    }

    @Test
    fun callConnectingOptions_enumsHaveValidLabelsAndIcons() {
        val instant = com.example.ui.viewmodel.CallConnectMode.INSTANT_CONNECT
        val ring = com.example.ui.viewmodel.CallConnectMode.RING_AND_ACCEPT
        assertTrue(instant.icon.isNotBlank())
        assertTrue(ring.icon.isNotBlank())

        val video = com.example.ui.viewmodel.CallType.VIDEO_CALL
        val audio = com.example.ui.viewmodel.CallType.AUDIO_FIRST
        assertEquals("Video Call", video.label)
        assertEquals("Audio Call", audio.label)

        val fast = com.example.ui.viewmodel.MatchingVibe.FAST_RANDOM
        assertEquals("All India", fast.label)
    }

    @Test
    fun chatSessionEntity_createsValidModel() {
        val session = com.example.data.local.ChatSessionEntity(
            partnerCodename = "MumbaiRocker #42",
            partnerLocation = "Mumbai, Maharashtra",
            partnerState = "Maharashtra",
            partnerLanguage = "mr",
            partnerInterests = "#Bollywood, #Music",
            callType = "Video Call",
            durationSeconds = 65,
            messageCount = 4,
            lastMessageExcerpt = "Namaste",
            lastTranslationExcerpt = "Hello"
        )
        assertEquals("MumbaiRocker #42", session.partnerCodename)
        assertEquals(65L, session.durationSeconds)
        assertEquals(4, session.messageCount)
    }
}
