package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.ChatSessionEntity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(context: Context) {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(
        context.applicationContext.getString(R.string.firestore_database_id)
    )

    private fun currentUserId(): String? = Firebase.auth.currentUser?.uid

    suspend fun syncUserProfile(
        codename: String,
        nativeLang: String,
        targetLang: String,
        state: String,
        totalMatches: Int
    ) {
        val uid = currentUserId() ?: return
        val userRef = db.collection("users").document(uid)

        val data = mapOf(
            "userId" to uid,
            "displayName" to (Firebase.auth.currentUser?.displayName ?: "Desi User"),
            "codename" to codename,
            "nativeLanguage" to nativeLang,
            "targetTranslationLanguage" to targetLang,
            "selectedState" to state,
            "totalMatches" to totalMatches,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error syncing user profile: ${e.message}")
        }
    }

    suspend fun backupSessionToCloud(session: ChatSessionEntity) {
        val uid = currentUserId() ?: return
        val sessionsRef = db.collection("users").document(uid).collection("sessions")

        val payload = mapOf(
            "userId" to uid,
            "partnerCodename" to session.partnerCodename,
            "partnerLocation" to session.partnerLocation,
            "partnerState" to session.partnerState,
            "partnerLanguage" to session.partnerLanguage,
            "callType" to session.callType,
            "durationSeconds" to session.durationSeconds,
            "messageCount" to session.messageCount,
            "lastMessage" to session.lastMessageExcerpt,
            "lastTranslation" to session.lastTranslationExcerpt,
            "timestamp" to FieldValue.serverTimestamp()
        )

        try {
            sessionsRef.add(payload).await()
            Log.d("FirestoreSync", "Session backed up to Firestore cloud successfully")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error backing up session to Firestore: ${e.message}")
        }
    }
}
