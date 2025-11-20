package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.AuthDataSource
import com.tdd.talktobook.data.dataStore.LocalDataStore
import com.tdd.talktobook.data.mapper.auth.EmailLogInMapper
import com.tdd.talktobook.data.mapper.auth.EmailSignUpMapper
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.request.auth.EmailSignUpRequestModel
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

    override suspend fun saveTokenModel(request: TokenModel): Flow<Result<Unit>> = flow {
        localDataStore.saveAccessToken(request.accessToken)
        localDataStore.saveRefreshToken(request.refreshToken)
        localDataStore.saveMetaData(request.metadataSuccess)
    }

    override suspend fun postEmailLogIn(request: EmailLogInRequestModel): Flow<Result<TokenModel>> =
        EmailLogInMapper.responseToModel(apiCall = {
            authDataSource.postEmailLogIn(
                request.email,
                request.password,
                request.deviceToken,
            )
        })

    override suspend fun postEmailSignUp(request: EmailSignUpRequestModel): Flow<Result<TokenModel>> =
        EmailSignUpMapper.responseToModel(apiCall = {
            authDataSource.postEmailSignUp(
                request.email,
                request.password,
            )
        })
}
