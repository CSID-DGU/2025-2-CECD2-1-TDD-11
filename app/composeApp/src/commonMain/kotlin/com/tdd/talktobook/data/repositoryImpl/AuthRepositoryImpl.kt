package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.AuthDataSource
import com.tdd.talktobook.data.dataStore.LocalDataStore
import com.tdd.talktobook.data.mapper.auth.EmailLogInMapper
import com.tdd.talktobook.data.mapper.auth.ReissueMapper
import com.tdd.talktobook.data.mapper.base.DefaultBooleanMapper
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailVerifyRequestModel
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import com.tdd.talktobook.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single(binds = [AuthRepository::class])
class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val localDataStore: LocalDataStore,
) : AuthRepository {
    override suspend fun saveToken(request: String): Flow<Result<Unit>> =
        flow {
            localDataStore.saveAccessToken(request)
        }

    override suspend fun saveTokenModel(request: TokenModel): Flow<Result<Unit>> =
        flow {
            localDataStore.saveAccessToken(request.accessToken)
            localDataStore.saveRefreshToken(request.refreshToken)
        }

    override suspend fun postEmailLogIn(request: EmailLogInRequestModel): Flow<Result<TokenModel>> =
        EmailLogInMapper.responseToModel(apiCall = {
            authDataSource.postEmailLogIn(
                request.email,
                request.password,
                request.deviceToken,
            )
        })

    override suspend fun postEmailSignUp(request: EmailSignUpRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            authDataSource.postEmailSignUp(
                request.email,
                request.password,
            )
        })

    override suspend fun postEmailVerify(request: EmailVerifyRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            authDataSource.postEmailVerification(request.email, request.code)
        })

    override suspend fun reissue(refresh: String): Flow<Result<TokenModel>> =
        ReissueMapper.responseToModel(apiCall = { authDataSource.reissue(refresh) })

    override suspend fun getStoredAccessToken(): Flow<Result<String>> =
        flow {
            localDataStore.accessToken.collect { token ->
                if (token != null) {
                    emit(Result.success(token))
                } else {
                    emit(Result.failure(Exception("[dataStore] access token is null")))
                }
            }
        }

    override suspend fun getStoredRefreshToken(): Flow<Result<String>> =
        flow {
            localDataStore.refreshToken.collect { token ->
                if (token != null) {
                    emit(Result.success(token))
                } else {
                    emit(Result.failure(Exception("[dataStore] refresh token is null")))
                }
            }
        }

    override suspend fun clearToken(): Flow<Result<Boolean>> =
        flow {
            localDataStore.clearTokens()
        }

    override suspend fun clearAllData(): Flow<Result<Boolean>> =
        flow {
            localDataStore.clearAll()
        }
}
