package com.tdd.domain.usecase.auth

import com.tdd.domain.base.UseCase
import com.tdd.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
): UseCase<String, Result<Boolean>>() {

    override suspend fun invoke(request: String): Flow<Result<Boolean>> =
        authRepository.saveUserId(request)
}