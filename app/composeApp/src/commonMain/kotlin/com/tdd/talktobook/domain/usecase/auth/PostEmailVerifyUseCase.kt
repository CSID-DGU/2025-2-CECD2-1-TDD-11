package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostEmailVerifyUseCase(
    private val repository: AuthRepository,
): UseCase<EmailVerifyRequestModel, Result<Boolean>>() {

    override suspend fun invoke(request: EmailVerifyRequestModel): Flow<Result<Boolean>> =
        repository.postEmailVerify(request)
}