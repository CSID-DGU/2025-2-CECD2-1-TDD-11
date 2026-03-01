package com.tdd.talktobook.data.dataStore

import dev.gitlive.firebase.firestore.FirebaseFirestore

class FireBaseDataStore(
    private val fireStore: FirebaseFirestore,
) {

    suspend fun postInquiry(
        userId: String, message: String, platform: String, createdAt: Long,
    ): String {
        val document = fireStore.collection("inquiries").add(
            mapOf(
                "userId" to userId,
                "message" to message,
                "platform" to platform,
                "createdAtMillis" to createdAt,
            )
        )

        return document.id
    }
}