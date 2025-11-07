package com.tdd.data.dataSource

import com.tdd.data.entity.request.auth.LogInRequestDto
import com.tdd.data.entity.response.auth.LogInResponseDto
import retrofit2.Response

interface AuthDataSource {
    suspend fun postLogIn(request: LogInRequestDto): Response<LogInResponseDto>
}