package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DeleteLocalTokenUseCase(
    private val repository: AuthRepository,
) : UseCase<Unit, Result<Boolean>>() {
    override suspend fun invoke(request: Unit): Flow<Result<Boolean>> =
        repository.clearToken()
}
