package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAutobiographyStatusUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Unit, Result<AutobiographyStatusType>>() {
    override suspend fun invoke(request: Unit): Flow<Result<AutobiographyStatusType>> =
        repository.getAutobiographyStatus()
}
