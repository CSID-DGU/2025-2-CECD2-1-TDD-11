package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.ChangeAutobiographyStatusRequestModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class ChangeAutobiographyStatusUseCase(
    private val repository: AutobiographyRepository,
): UseCase<ChangeAutobiographyStatusRequestModel, Result<Boolean>>() {

    override suspend fun invoke(request: ChangeAutobiographyStatusRequestModel): Flow<Result<Boolean>> =
        repository.patchChangeStatus(request)
}