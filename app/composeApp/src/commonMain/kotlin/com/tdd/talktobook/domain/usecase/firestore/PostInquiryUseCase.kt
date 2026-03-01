package com.tdd.talktobook.domain.usecase.firestore

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.firestore.InquiryRequestModel
import com.tdd.talktobook.domain.repository.FireStoreRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostInquiryUseCase(
    private val repository: FireStoreRepository
): UseCase<InquiryRequestModel, Result<String>>() {
    override suspend fun invoke(request: InquiryRequestModel): Flow<Result<String>> =
        repository.postInquiry(request)
}