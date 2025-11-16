package com.tdd.bookshelf.data.dataSource.dataSourceImpl

import com.tdd.bookshelf.data.dataSource.AuthDataSource
import com.tdd.bookshelf.data.service.AuthService
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
}
