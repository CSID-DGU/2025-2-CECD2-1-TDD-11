package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.firestore.FireStoreRequestModel
import kotlinx.coroutines.flow.Flow

interface FireStoreRepository {
    suspend fun postInquiry(request: FireStoreRequestModel): Flow<Result<String>>

    suspend fun postFeedback(request: FireStoreRequestModel): Flow<Result<String>>
}
