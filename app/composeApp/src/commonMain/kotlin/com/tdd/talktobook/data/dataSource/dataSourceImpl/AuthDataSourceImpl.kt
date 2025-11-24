package com.tdd.talktobook.data.dataSource.dataSourceImpl

import com.tdd.talktobook.data.dataSource.AuthDataSource
import com.tdd.talktobook.data.service.AuthService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [AuthDataSource::class])
class AuthDataSourceImpl(
    private val authService: AuthService,
) : AuthDataSource {
    override suspend fun postEmailLogIn(
        email: String,
        password: String,
        deviceToken: String,
    ): HttpResponse =
        authService.postEmailLogIn(email, password, deviceToken)

    override suspend fun postEmailSignUp(
        email: String,
        password: String,
    ): HttpResponse =
        authService.postEmailSignUp(email, password)

    override suspend fun postEmailVerification(email: String, code: String): HttpResponse =
        authService.postEmailVerify(email, code)

    override suspend fun deleteUser(): HttpResponse =
        authService.deleteUser()

    override suspend fun logOut(): HttpResponse =
        authService.logOut()
}
