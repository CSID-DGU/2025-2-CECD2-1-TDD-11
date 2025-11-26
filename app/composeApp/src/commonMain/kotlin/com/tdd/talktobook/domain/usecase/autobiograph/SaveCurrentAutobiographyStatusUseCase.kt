package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SaveCurrentAutobiographyStatusUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<AutobiographyStatusType, Result<Unit>>() {
    override suspend fun invoke(request: AutobiographyStatusType): Flow<Result<Unit>> =
        repository.saveCurrentAutobiographyStatus(request)
}
