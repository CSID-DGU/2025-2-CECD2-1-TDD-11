package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PatchCreateAutobiographyUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<CreateAutobiographyRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: CreateAutobiographyRequestModel): Flow<Result<Boolean>> =
        repository.patchCreateAutobiography(request)
}
