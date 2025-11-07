package com.tdd.data.service

import com.tdd.data.base.EndPoints
import com.tdd.data.entity.request.auth.LogInRequestDto
import com.tdd.data.entity.response.auth.LogInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST(EndPoints.Auth.AUTH)
    suspend fun login(
        @Body body: LogInRequestDto
    ): Response<LogInResponseDto>
}