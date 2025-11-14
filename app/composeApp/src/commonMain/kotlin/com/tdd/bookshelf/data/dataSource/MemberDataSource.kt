package com.tdd.bookshelf.data.dataSource

import io.ktor.client.statement.HttpResponse

interface MemberDataSource {
    suspend fun getMemberInfo(): HttpResponse

    suspend fun editMemberInfo(
        name: String,
        bornedAt: String,
        gender: String,
        hasChildren: Boolean,
        occupation: String,
        educationLevel: String,
        maritalStatus: String,
    ): HttpResponse

    suspend fun getMemberProfile(): HttpResponse
}
