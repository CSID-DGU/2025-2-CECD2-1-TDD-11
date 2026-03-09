package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
class GetRefreshTokenUseCase(
    private val repository: AuthRepository,
) : UseCase<Unit, Result<String>>() {
    override suspend fun invoke(request: Unit): Flow<Result<String>> =
        flow { emit(repository.getStoredRefreshToken()) }
}
