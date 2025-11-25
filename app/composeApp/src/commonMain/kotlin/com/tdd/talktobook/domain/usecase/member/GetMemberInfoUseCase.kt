package com.tdd.talktobook.domain.usecase.member

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import com.tdd.talktobook.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetMemberInfoUseCase(
    private val repository: MemberRepository,
) : UseCase<Unit, Result<MemberInfoResponseModel>>() {
    override suspend fun invoke(request: Unit): Flow<Result<MemberInfoResponseModel>> =
        repository.getMemberInfo()
}
