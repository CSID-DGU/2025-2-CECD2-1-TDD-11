package com.tdd.bookshelf.domain.usecase.auth

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class SaveTokenUseCase(
    private val repository: AuthRepository,
) : UseCase<String, Result<Unit>>() {
    override suspend fun invoke(request: String): Flow<Result<Unit>> =
        repository.saveToken(request)
}
