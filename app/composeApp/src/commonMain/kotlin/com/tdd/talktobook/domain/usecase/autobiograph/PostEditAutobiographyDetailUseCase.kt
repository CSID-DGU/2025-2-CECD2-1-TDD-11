package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostEditAutobiographyDetailUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<EditAutobiographyDetailRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: EditAutobiographyDetailRequestModel): Flow<Result<Boolean>> =
        repository.postEditAutobiographiesDetail(request)
}
