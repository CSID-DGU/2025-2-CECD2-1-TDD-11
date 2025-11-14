package com.tdd.bookshelf.data.dataSource.dataSourceImpl

import com.tdd.bookshelf.data.dataSource.MemberDataSource
import com.tdd.bookshelf.data.service.MemberService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [MemberDataSource::class])
class MemberDataSourceImpl(
    private val memberService: MemberService,
) : MemberDataSource {
    override suspend fun getMemberInfo(): HttpResponse =
        memberService.getMemberInfo()

    override suspend fun editMemberInfo(
        name: String,
        bornedAt: String,
        gender: String,
        hasChildren: Boolean,
        occupation: String,
        educationLevel: String,
        maritalStatus: String,
    ): HttpResponse =
        memberService.editMemberInfo(
            name,
            bornedAt,
            gender,
            hasChildren,
            occupation,
            educationLevel,
            maritalStatus,
        )

    override suspend fun getMemberProfile(): HttpResponse =
        memberService.getMemberProfile()
}
