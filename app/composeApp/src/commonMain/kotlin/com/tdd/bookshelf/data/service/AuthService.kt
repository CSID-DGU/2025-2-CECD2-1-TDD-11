package com.tdd.bookshelf.data.service

import com.tdd.bookshelf.data.base.EndPoints
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import io.ktor.client.statement.HttpResponse

interface AuthService {
    @Multipart
    @POST(EndPoints.Auth.EMAILLOGIN)
    suspend fun postEmailLogIn(
        @Part("email") email: String,
        @Part("password") password: String,
        @Part("deviceToken") deviceToken: String = "",
    ): HttpResponse

    @Multipart
    @POST(EndPoints.Auth.EMAILSIGNUP)
    suspend fun postEmailSignUp(
        @Part("email") email: String,
        @Part("password") password: String,
    ): HttpResponse
}
