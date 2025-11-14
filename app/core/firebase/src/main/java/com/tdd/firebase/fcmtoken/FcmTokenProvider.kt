package com.tdd.firebase.fcmtoken

interface FcmTokenProvider {
    suspend fun getFcmToken(): String
}