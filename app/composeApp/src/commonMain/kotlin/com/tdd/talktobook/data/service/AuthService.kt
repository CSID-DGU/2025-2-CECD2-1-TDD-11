package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
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

    @Multipart
    @POST(EndPoints.Autobiography.COSHOW_START_PROGRESS)
    suspend fun postCoShowInit(
        @Part("theme") theme: String,
        @Part("reason") reason: String,
    ): HttpResponse

    @GET(EndPoints.Autobiography.COSHOW_CREATE_AUTOBIOGRAPHY)
    suspend fun getCoShowGenerate(
        @Path("autobiographyId") autobiographyId: Int,
//        @Query("requestDto") request: GetCoShowGenerateRequestDto
        @Query("name") name: String,
    ): HttpResponse

    @GET(EndPoints.Interview.COSHOW_INTERVIEW_CONVERSATIONS)
    suspend fun getCoShowInterviewConversation(
        @Path("interviewId") interviewId: Int,
    ): HttpResponse

    @Multipart
    @POST(EndPoints.Interview.COSHOW_INTERVIEW_QUESTION)
    suspend fun postCoShowInterviewAnswer(
        @Path("interviewId") interviewId: Int,
        @Part("answerText") answerText: String,
    ): HttpResponse
}
