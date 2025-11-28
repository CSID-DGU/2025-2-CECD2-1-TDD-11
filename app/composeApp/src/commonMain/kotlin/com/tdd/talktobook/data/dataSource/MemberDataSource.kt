package com.tdd.talktobook.data.dataSource

import io.ktor.client.statement.HttpResponse

interface MemberDataSource {
    suspend fun getMemberInfo(): HttpResponse

    suspend fun editMemberInfo(
        gender: String,
        occupation: String,
        ageGroup: String,
    ): HttpResponse

    suspend fun getMemberProfile(): HttpResponse

    suspend fun deleteUser(): HttpResponse

    suspend fun logOut(): HttpResponse
}
