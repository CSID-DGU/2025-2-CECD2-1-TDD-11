package com.tdd.talktobook.data.dataSource

import io.ktor.client.statement.HttpResponse

interface AuthDataSource {
    suspend fun postEmailLogIn(
        email: String,
        password: String,
        deviceToken: String,
    ): HttpResponse

    suspend fun postEmailSignUp(
        email: String,
        password: String,
    ): HttpResponse

    suspend fun postEmailVerification(
        email: String,
        code: String,
    ): HttpResponse

    suspend fun deleteUser(): HttpResponse

    suspend fun logOut(): HttpResponse

    suspend fun reissue(
        refreshToken: String,
    ): HttpResponse
}
