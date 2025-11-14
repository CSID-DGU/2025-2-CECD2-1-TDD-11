package com.tdd.bookshelf.data.repositoryImpl

import com.tdd.bookshelf.data.dataSource.AuthDataSource
import com.tdd.bookshelf.data.dataStore.LocalDataStore
import com.tdd.bookshelf.data.mapper.auth.EmailLogInMapper
import com.tdd.bookshelf.data.mapper.auth.EmailSignUpMapper
import com.tdd.bookshelf.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.bookshelf.domain.entity.request.auth.EmailSignUpRequestModel
import com.tdd.bookshelf.domain.entity.response.auth.AccessTokenModel
import com.tdd.bookshelf.domain.repository.AuthRepository
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

    override suspend fun postEmailLogIn(request: EmailLogInRequestModel): Flow<Result<AccessTokenModel>> =
        EmailLogInMapper.responseToModel(apiCall = {
            authDataSource.postEmailLogIn(
                request.email,
                request.password,
                request.deviceToken,
            )
        })

    override suspend fun postEmailSignUp(request: EmailSignUpRequestModel): Flow<Result<AccessTokenModel>> =
        EmailSignUpMapper.responseToModel(apiCall = {
            authDataSource.postEmailSignUp(
                request.email,
                request.password,
            )
        })
}
