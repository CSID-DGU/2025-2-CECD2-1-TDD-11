package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.member.MemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun getMemberInfo(): Flow<Result<MemberInfoResponseModel>>

    suspend fun putEditMemberInfo(request: MemberInfoModel): Flow<Result<Boolean>>
}
