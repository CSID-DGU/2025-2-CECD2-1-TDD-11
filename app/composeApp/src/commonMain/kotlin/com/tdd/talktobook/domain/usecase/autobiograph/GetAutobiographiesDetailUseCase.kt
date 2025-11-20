package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAutobiographiesDetailUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Int, Result<AutobiographiesDetailModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<AutobiographiesDetailModel>> =
        repository.getAutobiographiesDetail(request)
}
