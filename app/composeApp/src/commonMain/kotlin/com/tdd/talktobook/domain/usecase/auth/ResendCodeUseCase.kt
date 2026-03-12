package com.tdd.talktobook.domain.usecase.auth

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ResendCodeUseCase(
    private val repository: AuthRepository,
) : UseCase<String, Result<Boolean>>() {
    override suspend fun invoke(request: String): Flow<Result<Boolean>> =
        repository.resendCode(request)
}
