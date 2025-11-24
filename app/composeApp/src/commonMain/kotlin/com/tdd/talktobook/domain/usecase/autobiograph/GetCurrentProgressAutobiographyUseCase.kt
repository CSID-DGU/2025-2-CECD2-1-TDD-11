package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetCurrentProgressAutobiographyUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Unit, Result<CurrentProgressAutobiographyModel>>() {
    override suspend fun invoke(request: Unit): Flow<Result<CurrentProgressAutobiographyModel>> =
        repository.getCurrentProgressAutobiography()
}
