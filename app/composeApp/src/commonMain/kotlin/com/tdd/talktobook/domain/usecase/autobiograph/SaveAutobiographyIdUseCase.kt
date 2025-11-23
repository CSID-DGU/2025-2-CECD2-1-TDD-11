package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SaveAutobiographyIdUseCase(
    private val repository: AutobiographyRepository,
): UseCase<Int, Result<Unit>>() {

    override suspend fun invoke(request: Int): Flow<Result<Unit>> =
        repository.saveAutobiographyId(request)
}