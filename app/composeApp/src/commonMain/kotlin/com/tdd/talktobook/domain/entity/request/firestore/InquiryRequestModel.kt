package com.tdd.talktobook.domain.entity.request.firestore

import kotlinx.datetime.Clock

data class InquiryRequestModel (
    val userId: String = "",
    val message: String = "",
    val platform: String = "",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
)