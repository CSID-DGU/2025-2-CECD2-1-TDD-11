package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.StartProgressRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostStartProgressUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<StartProgressRequestModel, Result<InterviewAutobiographyModel>>() {
    override suspend fun invoke(request: StartProgressRequestModel): Flow<Result<InterviewAutobiographyModel>> =
        repository.postStartProgress(request)
}
