package com.tdd.talktobook.domain.usecase.autobiograph

import com.tdd.talktobook.domain.base.UseCase
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class GetSelectedThemeUseCase(
    private val repository: AutobiographyRepository,
) : UseCase<Int, Result<SelectedThemeModel>>() {
    override suspend fun invoke(request: Int): Flow<Result<SelectedThemeModel>> =
        repository.getSelectedTheme(request)
}
