package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataStore.FireBaseDataStore
import com.tdd.talktobook.data.entity.response.api.ApiException
import com.tdd.talktobook.domain.entity.request.firestore.FireStoreRequestModel
import com.tdd.talktobook.domain.repository.FireStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single(binds = [FireStoreRepository::class])
class FireStoreRepositoryImpl(
    private val fireBaseDataStore: FireBaseDataStore,
) : FireStoreRepository {
    override suspend fun postInquiry(request: FireStoreRequestModel): Flow<Result<String>> = flow {
        runCatching { fireBaseDataStore.postInquiry(request.userId, request.message, request.platform, request.createdAt) }
            .onSuccess { emit(Result.success(it)) }
            .onFailure { emit(Result.failure(ApiException(400, "[ktor] fireStore Post Inquiry error"))) }
    }

    override suspend fun postFeedback(request: FireStoreRequestModel): Flow<Result<String>> = flow {
        runCatching { fireBaseDataStore.postFeedback(request.userId, request.message, request.platform, request.createdAt) }
            .onSuccess { emit(Result.success(it)) }
            .onFailure { emit(Result.failure(ApiException(400, "[ktor] fireStore Post Feedback error"))) }
    }
}