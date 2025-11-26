package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.request.autobiography.GetCoShowGenerateRequestModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetCoShowGenerateUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<GetCoShowGenerateRequestModel, Result<Boolean>>() {

    override suspend fun invoke(request: GetCoShowGenerateRequestModel): Flow<Result<Boolean>> =
        repository.getCoShowGenerate(request)
}