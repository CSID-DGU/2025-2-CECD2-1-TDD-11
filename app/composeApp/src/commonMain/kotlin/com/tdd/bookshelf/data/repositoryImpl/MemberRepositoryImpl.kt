package com.tdd.bookshelf.data.repositoryImpl

import com.tdd.bookshelf.data.dataSource.MemberDataSource
import com.tdd.bookshelf.data.mapper.base.DefaultBooleanMapper
import com.tdd.bookshelf.data.mapper.member.MemberInfoMapper
import com.tdd.bookshelf.data.mapper.member.MemberProfileMapper
import com.tdd.bookshelf.domain.entity.request.member.EditMemberInfoModel
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel
import com.tdd.bookshelf.domain.entity.response.member.MemberProfileModel
import com.tdd.bookshelf.domain.repository.MemberRepository
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
                request.name,
                request.bornedAt,
                request.gender,
                request.hasChildren,
                request.occupation,
                request.educationLevel,
                request.maritalStatus,
            )
        })

    override suspend fun getMemberProfile(): Flow<Result<MemberProfileModel>> =
        MemberProfileMapper.responseToModel(apiCall = { memberDataSource.getMemberProfile() })
}
