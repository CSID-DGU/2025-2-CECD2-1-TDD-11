package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostEmailSignUpUseCase(
    private val repository: AuthRepository,
) : UseCase<EmailSignUpRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: EmailSignUpRequestModel): Flow<Result<Boolean>> =
        repository.postEmailSignUp(request)
}
