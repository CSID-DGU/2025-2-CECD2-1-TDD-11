package com.tdd.data.repositoryImpl

import com.tdd.data.dataSource.AuthDataSource
import com.tdd.data.dataStore.LocalDataStore
import com.tdd.data.mapper.auth.LogInMapper
import com.tdd.data.mapper.auth.LogInMapper.toDto
import com.tdd.domain.entity.request.auth.AuthRequestModel
import com.tdd.domain.entity.response.auth.AuthResponseModel
import com.tdd.domain.repository.AuthRepository
import com.tdd.firebase.fcmtoken.FcmTokenProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val localDataStore: LocalDataStore,
    private val fcmTokenProvider: FcmTokenProvider,
) : AuthRepository {

    override suspend fun postLogIn(request: AuthRequestModel): Flow<Result<AuthResponseModel>> =
        LogInMapper.responseToModel(apiCall = { authDataSource.postLogIn(request.toDto()) })

    override suspend fun saveUserId(request: String): Flow<Result<Boolean>> = flow {
        localDataStore.saveUserId(request)
    }

    override suspend fun getFcmToken(): Flow<Result<String>> = flow {
        fcmTokenProvider.getFcmToken()
    }
}