package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.MemberDataSource
import com.tdd.talktobook.data.mapper.base.DefaultBooleanMapper
import com.tdd.talktobook.data.mapper.member.MemberInfoMapper
import com.tdd.talktobook.domain.entity.request.member.MemberInfoModel
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [MemberRepository::class])
class MemberRepositoryImpl(
    private val memberDataSource: MemberDataSource,
) : MemberRepository {
    override suspend fun getMemberInfo(): Flow<Result<MemberInfoResponseModel>> =
        MemberInfoMapper.responseToModel(apiCall = { memberDataSource.getMemberInfo() })

    override suspend fun putEditMemberInfo(request: MemberInfoModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            memberDataSource.editMemberInfo(
                request.gender,
                request.occupation,
                request.ageGroup,
            )
        })

    override suspend fun deleteUser(): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            memberDataSource.deleteUser()
        })

    override suspend fun logOut(): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = { memberDataSource.logOut() })
}
