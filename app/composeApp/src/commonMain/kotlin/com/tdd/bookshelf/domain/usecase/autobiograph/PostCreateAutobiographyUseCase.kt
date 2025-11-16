package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCreateAutobiographyUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<CreateAutobiographyRequestModel, Result<Boolean>>() {
    override suspend fun invoke(request: CreateAutobiographyRequestModel): Flow<Result<Boolean>> =
        repository.postCreateAutobiographies(request)
}
