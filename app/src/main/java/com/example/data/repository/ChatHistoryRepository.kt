package com.example.data.repository

import com.example.data.local.ChatSessionDao
import com.example.data.local.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

class ChatHistoryRepository(private val dao: ChatSessionDao) {

    val allSessions: Flow<List<ChatSessionEntity>> = dao.getAllSessions()

    suspend fun saveSession(session: ChatSessionEntity): Long {
        return dao.insertSession(session)
    }

    suspend fun deleteSession(id: Long) {
        dao.deleteSessionById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllSessions()
    }
}
