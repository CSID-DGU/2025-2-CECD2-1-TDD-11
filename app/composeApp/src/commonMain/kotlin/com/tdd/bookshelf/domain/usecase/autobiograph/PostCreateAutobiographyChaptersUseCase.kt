package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCreateAutobiographyChaptersUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<CreateAutobiographyChaptersRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: CreateAutobiographyChaptersRequestModel): Flow<Result<Boolean>> =
        repository.postCreateChapterList(request)
}
