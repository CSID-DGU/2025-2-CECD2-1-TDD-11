package com.tdd.talktobook.data.dataSource.dataSourceImpl

import com.tdd.talktobook.data.dataSource.MemberDataSource
import com.tdd.talktobook.data.service.MemberService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [MemberDataSource::class])
class MemberDataSourceImpl(
    private val memberService: MemberService,
) : MemberDataSource {
    override suspend fun getMemberInfo(): HttpResponse =
        memberService.getMemberInfo()

    override suspend fun editMemberInfo(
        gender: String,
        occupation: String,
        ageGroup: String,
    ): HttpResponse =
        memberService.editMemberInfo(
            gender,
            occupation,
            ageGroup,
        )

    override suspend fun getMemberProfile(): HttpResponse =
        memberService.getMemberProfile()
}
