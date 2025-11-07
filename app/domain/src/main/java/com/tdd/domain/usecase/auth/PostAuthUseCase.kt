package com.tdd.domain.usecase.auth

import com.tdd.domain.base.UseCase
import com.tdd.domain.entity.request.auth.AuthRequestModel
import com.tdd.domain.entity.response.auth.AuthResponseModel
import com.tdd.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostAuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
): UseCase<AuthRequestModel, Result<AuthResponseModel>>() {

    override suspend fun invoke(request: AuthRequestModel): Flow<Result<AuthResponseModel>> =
        authRepository.postLogIn(request)
}