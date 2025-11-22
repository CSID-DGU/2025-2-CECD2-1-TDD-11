package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun getMemberInfo(): Flow<Result<MemberInfoModel>>

    suspend fun putEditMemberInfo(request: MemberInfoModel): Flow<Result<Boolean>>
}
