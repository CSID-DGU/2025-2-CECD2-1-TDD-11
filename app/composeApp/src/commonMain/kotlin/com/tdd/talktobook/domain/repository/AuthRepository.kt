package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.entity.response.auth.AccessTokenModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun saveToken(request: String): Flow<Result<Unit>>

    suspend fun postEmailLogIn(request: EmailLogInRequestModel): Flow<Result<AccessTokenModel>>

    suspend fun postEmailSignUp(request: EmailSignUpRequestModel): Flow<Result<AccessTokenModel>>

    suspend fun deleteUser(): Flow<Result<Unit>>
}
