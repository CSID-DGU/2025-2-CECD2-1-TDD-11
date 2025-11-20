package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.AutobiographyIdResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

object GetCurrentProgressMapper: BaseMapper() {
    private val json = Json { ignoreUnknownKeys = true }

    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<CurrentProgressAutobiographyModel>> =
        flow {
            try {
                val response = apiCall()
                val body = response.bodyAsText()

                if (response.status.isSuccess()) {
                    val dto = json.decodeFromString<AutobiographyIdResponseDto>(body)

                    emit(Result.success(CurrentProgressAutobiographyModel(dto.autobiographyId, true)))
                } else {
                    val errorBody = runCatching { response.bodyAsText() }.getOrNull()
                    emit(Result.success(CurrentProgressAutobiographyModel(0, false, message = body)))
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
}