package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.member.EditMemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberProfileModel
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    suspend fun getMemberInfo(): Flow<Result<MemberInfoModel>>

    suspend fun putEditMemberInfo(request: EditMemberInfoModel): Flow<Result<Boolean>>

    suspend fun getMemberProfile(): Flow<Result<MemberProfileModel>>
}
