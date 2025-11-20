package com.tdd.talktobook.domain.usecase.member

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.member.EditMemberInfoModel
import com.tdd.talktobook.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PutEditMemberInfoUseCase(
    private val repository: MemberRepository,
) : UseCase<EditMemberInfoModel, Result<Boolean>>() {
    override suspend fun invoke(request: EditMemberInfoModel): Flow<Result<Boolean>> =
        repository.putEditMemberInfo(request)
}
