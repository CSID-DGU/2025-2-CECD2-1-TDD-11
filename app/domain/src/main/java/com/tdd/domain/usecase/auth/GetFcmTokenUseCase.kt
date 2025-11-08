package com.tdd.domain.usecase.auth

import com.tdd.domain.base.UseCase
import com.tdd.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFcmTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) : UseCase<Unit, Result<String>>() {

    override suspend fun invoke(request: Unit): Flow<Result<String>> =
        authRepository.getFcmToken()
}