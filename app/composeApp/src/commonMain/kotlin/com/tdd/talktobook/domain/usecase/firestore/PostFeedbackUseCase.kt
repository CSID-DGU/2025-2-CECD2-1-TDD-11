package com.tdd.talktobook.domain.usecase.firestore

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.firestore.FireStoreRequestModel
import com.tdd.talktobook.domain.repository.FireStoreRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostFeedbackUseCase(
    private val repository: FireStoreRepository,
) : UseCase<FireStoreRequestModel, Result<String>>() {
    override suspend fun invoke(request: FireStoreRequestModel): Flow<Result<String>> =
        repository.postFeedback(request)
}
