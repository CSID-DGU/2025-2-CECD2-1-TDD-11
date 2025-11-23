package com.tdd.talktobook.domain.usecase.member

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.member.MemberProfileModel
import com.tdd.talktobook.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetMemberProfileUseCase(
    private val repository: MemberRepository,
) : UseCase<Unit, Result<MemberProfileModel>>() {
    override suspend fun invoke(request: Unit): Flow<Result<MemberProfileModel>> =
        repository.getMemberProfile()
}
