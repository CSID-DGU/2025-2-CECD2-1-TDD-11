package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Part
import io.ktor.client.statement.HttpResponse

interface MemberService {
    @GET(EndPoints.Member.MEMBER)
    suspend fun getMemberInfo(): HttpResponse

    @Multipart
    @PUT(EndPoints.Member.MEMBER)
    suspend fun editMemberInfo(
        @Part("gender") gender: String,
        @Part("occupation") occupation: String,
        @Part("ageGroup") ageGroup: String,
    ): HttpResponse

    @GET(EndPoints.Member.PROFILE)
    suspend fun getMemberProfile(): HttpResponse
}
