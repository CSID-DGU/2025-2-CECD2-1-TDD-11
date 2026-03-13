package com.tdd.talktobook.domain.entity.request.firestore

data class FireStoreRequestModel(
    val userId: String = "",
    val message: String = "",
    val platform: String = "",
    val createdAt: String = "",
)
