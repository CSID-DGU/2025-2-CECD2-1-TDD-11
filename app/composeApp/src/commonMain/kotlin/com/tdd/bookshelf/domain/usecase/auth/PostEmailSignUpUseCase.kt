package com.tdd.bookshelf.domain.usecase.auth

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.bookshelf.domain.entity.response.auth.AccessTokenModel
import com.tdd.bookshelf.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostEmailSignUpUseCase(
    private val repository: AuthRepository,
) : UseCase<EmailSignUpRequestModel, Result<AccessTokenModel>>() {
    override suspend fun invoke(request: EmailSignUpRequestModel): Flow<Result<AccessTokenModel>> =
        repository.postEmailSignUp(request)
}
