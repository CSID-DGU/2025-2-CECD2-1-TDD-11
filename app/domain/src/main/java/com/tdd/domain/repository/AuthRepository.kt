package com.tdd.domain.repository

import com.tdd.domain.entity.request.auth.AuthRequestModel
import com.tdd.domain.entity.response.auth.AuthResponseModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun postLogIn(request: AuthRequestModel): Flow<Result<AuthResponseModel>>
    suspend fun saveUserId(request: String): Flow<Result<Boolean>>

    suspend fun getFcmToken(): Flow<Result<String>>
}