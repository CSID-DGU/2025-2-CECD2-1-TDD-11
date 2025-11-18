package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.response.auth.AccessTokenModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostEmailLogInUseCase(
    private val repository: AuthRepository,
) : UseCase<EmailLogInRequestModel, Result<AccessTokenModel>>() {
    override suspend fun invoke(request: EmailLogInRequestModel): Flow<Result<AccessTokenModel>> =
        repository.postEmailLogIn(request)
}
