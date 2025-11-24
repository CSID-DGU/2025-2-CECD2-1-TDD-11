package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAccessTokenUseCase(
    private val repository: AuthRepository,
): UseCase<Unit, Result<String>>() {

    override suspend fun invoke(request: Unit): Flow<Result<String>> =
        repository.getStoredAccessToken()
}