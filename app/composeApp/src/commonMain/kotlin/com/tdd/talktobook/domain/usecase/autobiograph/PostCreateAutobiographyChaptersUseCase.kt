package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCreateAutobiographyChaptersUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<CreateAutobiographyChaptersRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: CreateAutobiographyChaptersRequestModel): Flow<Result<Boolean>> =
        repository.postCreateChapterList(request)
}
