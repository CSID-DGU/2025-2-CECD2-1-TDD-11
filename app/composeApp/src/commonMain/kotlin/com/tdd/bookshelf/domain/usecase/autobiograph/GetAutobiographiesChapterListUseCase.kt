package com.tdd.bookshelf.domain.usecase.autobiograph

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterListModel
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetAutobiographiesChapterListUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Unit, Result<ChapterListModel>>() {
    override suspend fun invoke(request: Unit): Flow<Result<ChapterListModel>> =
        repository.getAutobiographyChapter()
}
