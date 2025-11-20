package com.tdd.talktobook.data.mapper.base

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DefaultBooleanNotJsonMapper {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<Boolean>> =
        flow {
            try {
                val response = apiCall()

                if (response.status.isSuccess()) {
                    emit(Result.success(true))
                } else {
                    val errorBody = runCatching { response.bodyAsText() }.getOrNull()
                    emit(
                        Result.failure(
                            Throwable("[ktor] ${response.status} ${errorBody ?: ""}")
                        )
                    )
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
}