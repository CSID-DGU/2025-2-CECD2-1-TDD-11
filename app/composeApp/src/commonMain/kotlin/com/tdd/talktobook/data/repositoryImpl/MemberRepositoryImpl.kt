package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.MemberDataSource
import com.tdd.talktobook.data.mapper.base.DefaultBooleanMapper
import com.tdd.talktobook.data.mapper.member.MemberInfoMapper
import com.tdd.talktobook.data.mapper.member.MemberProfileMapper
import com.tdd.talktobook.domain.entity.request.member.EditMemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberProfileModel
import com.tdd.talktobook.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [MemberRepository::class])
class MemberRepositoryImpl(
    private val memberDataSource: MemberDataSource,
) : MemberRepository {
    override suspend fun getMemberInfo(): Flow<Result<MemberInfoModel>> =
        MemberInfoMapper.responseToModel(apiCall = { memberDataSource.getMemberInfo() })

    override suspend fun putEditMemberInfo(request: EditMemberInfoModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            memberDataSource.editMemberInfo(
                request.gender,
                request.occupation,
                request.ageGroup
            )
        })

    override suspend fun getMemberProfile(): Flow<Result<MemberProfileModel>> =
        MemberProfileMapper.responseToModel(apiCall = { memberDataSource.getMemberProfile() })
}
