package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetCurrentCoShowProgressUseCase(
    private val repository: AutobiographyRepository,
): UseCase<Int, Result<CurrentInterviewProgressModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<CurrentInterviewProgressModel>> =
        repository.getCurrentCoShowProgress(request)
}