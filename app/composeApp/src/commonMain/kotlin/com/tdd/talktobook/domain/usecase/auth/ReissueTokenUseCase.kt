package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ReissueTokenUseCase(
    private val repository: AuthRepository,
) : UseCase<String, Result<TokenModel>>() {
    override suspend fun invoke(request: String): Flow<Result<TokenModel>> =
        repository.reissue(request)
}
