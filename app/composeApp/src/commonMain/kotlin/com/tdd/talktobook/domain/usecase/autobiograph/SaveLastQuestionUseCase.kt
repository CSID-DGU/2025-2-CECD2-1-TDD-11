package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SaveLastQuestionUseCase(
    private val repository: AutobiographyRepository,
): UseCase<String, Result<Unit>>() {
    override suspend fun invoke(request: String): Flow<Result<Unit>> =
        repository.saveLastQuestion(request)
}