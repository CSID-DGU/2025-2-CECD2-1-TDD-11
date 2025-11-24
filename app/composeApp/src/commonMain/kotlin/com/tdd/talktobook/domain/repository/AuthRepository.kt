package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun saveToken(request: String): Flow<Result<Unit>>

    suspend fun saveTokenModel(request: TokenModel): Flow<Result<Unit>>

    suspend fun postEmailLogIn(request: EmailLogInRequestModel): Flow<Result<TokenModel>>

    suspend fun postEmailSignUp(request: EmailSignUpRequestModel): Flow<Result<Boolean>>

    suspend fun postEmailVerify(request: EmailVerifyRequestModel): Flow<Result<Boolean>>

    suspend fun deleteUser(): Flow<Result<Boolean>>

    suspend fun logOut(): Flow<Result<Boolean>>

    suspend fun reissue(refresh: String): Flow<Result<TokenModel>>

    suspend fun getStoredAccessToken(): Flow<Result<String>>

    suspend fun getStoredRefreshToken(): Flow<Result<String>>
}
