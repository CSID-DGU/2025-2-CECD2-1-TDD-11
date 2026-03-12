package com.tdd.talktobook.data.dataStore

import dev.gitlive.firebase.firestore.FirebaseFirestore

class FireBaseDataStore(
    private val fireStore: FirebaseFirestore,
) {
    suspend fun postInquiry(
        userId: String,
        message: String,
        platform: String,
        createdAt: String,
    ): String {
        val document =
            fireStore.collection("inquiries").add(
                mapOf(
                    "userId" to userId,
                    "message" to message,
                    "platform" to platform,
                    "createdAt" to createdAt,
                ),
            )

        return document.id
    }

    suspend fun postFeedback(
        userId: String,
        message: String,
        platform: String,
        createdAt: String,
    ): String {
        val document =
            fireStore.collection("feedbacks").add(
                mapOf(
                    "userId" to userId,
                    "message" to message,
                    "platform" to platform,
                    "createdAt" to createdAt,
                ),
            )

        return document.id
    }
}
