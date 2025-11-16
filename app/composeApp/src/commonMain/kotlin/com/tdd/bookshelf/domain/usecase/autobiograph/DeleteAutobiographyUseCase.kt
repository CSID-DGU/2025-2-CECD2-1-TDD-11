package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class DeleteAutobiographyUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Int, Result<Boolean>>() {
    override suspend fun invoke(request: Int): Flow<Result<Boolean>> =
        repository.deleteAutobiography(request)
}
