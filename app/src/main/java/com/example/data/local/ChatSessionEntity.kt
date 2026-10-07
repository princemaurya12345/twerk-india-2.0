package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partnerCodename: String,
    val partnerLocation: String,
    val partnerState: String,
    val partnerLanguage: String,
    val partnerInterests: String,
    val callType: String,
    val durationSeconds: Long,
    val messageCount: Int,
    val lastMessageExcerpt: String = "",
    val lastTranslationExcerpt: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val avatarGradientStart: Long = 0xFFFF5722,
    val avatarGradientEnd: Long = 0xFF9C27B0
)
