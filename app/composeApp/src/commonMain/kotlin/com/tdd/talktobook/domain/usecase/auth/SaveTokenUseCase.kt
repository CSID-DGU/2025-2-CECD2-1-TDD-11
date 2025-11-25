package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SaveTokenUseCase(
    private val repository: AuthRepository,
) : UseCase<TokenModel, Result<Unit>>() {
    override suspend fun invoke(request: TokenModel): Flow<Result<Unit>> =
        repository.saveTokenModel(request)
}
