package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAutobiographiesDetailUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Int, Result<AutobiographiesDetailModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<AutobiographiesDetailModel>> =
        repository.getAutobiographiesDetail(request)
}
