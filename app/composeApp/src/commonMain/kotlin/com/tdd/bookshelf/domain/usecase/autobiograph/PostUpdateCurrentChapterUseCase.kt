package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostUpdateCurrentChapterUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Unit, Result<Boolean>>() {
    override suspend fun invoke(request: Unit): Flow<Result<Boolean>> =
        repository.postUpdateCurrentChapter()
}
