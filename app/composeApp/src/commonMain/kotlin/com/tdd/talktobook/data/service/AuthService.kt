package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import de.jensklingenberg.ktorfit.http.DELETE
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

    @Multipart
    @POST(EndPoints.Auth.EMAILVERIFY)
    suspend fun postEmailVerify(
        @Part("email") email: String,
        @Part("verificationCode") code: String,
    ): HttpResponse

    @Multipart
    @POST(EndPoints.Auth.REISSUE)
    suspend fun reissue(
        @Part("refreshToken") refreshToken: String,
    ): HttpResponse
}
