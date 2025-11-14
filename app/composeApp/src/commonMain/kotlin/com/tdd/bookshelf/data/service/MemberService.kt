package com.tdd.bookshelf.data.service

import com.tdd.bookshelf.data.base.EndPoints
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
        @Part("name") name: String,
        @Part("bornedAt") bornedAt: String,
        @Part("gender") gender: String,
        @Part("hasChildren") hasChildren: Boolean,
        @Part("occupation") occupation: String,
        @Part("educationLevel") educationLevel: String,
        @Part("maritalStatus") maritalStatus: String,
    ): HttpResponse

    @GET(EndPoints.Member.PROFILE)
    suspend fun getMemberProfile(): HttpResponse
}
