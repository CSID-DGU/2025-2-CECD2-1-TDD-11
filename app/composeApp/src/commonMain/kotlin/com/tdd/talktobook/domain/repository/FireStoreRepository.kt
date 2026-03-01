package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.firestore.InquiryRequestModel
import kotlinx.coroutines.flow.Flow

interface FireStoreRepository {
    suspend fun postInquiry(request: InquiryRequestModel): Flow<Result<String>>
}