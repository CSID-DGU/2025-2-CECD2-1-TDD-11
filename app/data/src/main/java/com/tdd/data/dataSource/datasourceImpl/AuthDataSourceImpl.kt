package com.tdd.data.dataSource.datasourceImpl

import com.tdd.data.dataSource.AuthDataSource
import com.tdd.data.entity.request.auth.LogInRequestDto
import com.tdd.data.entity.response.auth.LogInResponseDto
import com.tdd.data.service.AuthService
import retrofit2.Response
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val authService: AuthService,
) : AuthDataSource {

    override suspend fun postLogIn(request: LogInRequestDto): Response<LogInResponseDto> =
        authService.login(request)
}