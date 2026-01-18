package com.tdd.talktobook.data.base

import com.tdd.talktobook.data.entity.response.api.ApiError
import com.tdd.talktobook.data.entity.response.api.ApiException
import com.tdd.talktobook.data.entity.response.api.ApiStatusResponse
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

abstract class BaseMapper {
    private val json = Json { ignoreUnknownKeys = true }

    fun <DTO, MODEL> baseMapper(
        apiCall: suspend () -> HttpResponse?,
        successDeserializer: KSerializer<DTO>? = null,
        responseToModel: (DTO?) -> MODEL,
    ): Flow<Result<MODEL>> =
        flow {
            try {
                val response = apiCall()
                val httpStatus = response?.status
                val responseBody = response?.bodyAsText() ?: ""

                if (httpStatus != null && httpStatus.value in 200..299) {
                    val model =
                        when {
                            httpStatus == HttpStatusCode.NoContent || responseBody.isEmpty() || successDeserializer == null -> {
                                responseToModel(null)
                            }
                            else -> {
                                val dto = json.decodeFromString(successDeserializer, responseBody)
                                responseToModel(dto)
                            }
                        }
                    emit(Result.success(model))
                } else {
                    val error =
                        if (responseBody.isNotBlank()) {
                            runCatching {
                                json.decodeFromString(
                                    ApiStatusResponse.serializer(),
                                    responseBody,
                                )
                            }.getOrNull()
                        } else {
                            null
                        }
                    val msg = error?.message ?: "[ktor] http error ${httpStatus?.value ?: "Unknown"}"
                    val code = error?.statusCode ?: httpStatus?.value ?: 0
                    emit(Result.failure(ApiException(code, msg)))
                }
            } catch (e: ResponseException) {
                val httpStatus = e.response.status
                val text = runCatching { e.response.bodyAsText() }.getOrDefault("")

                val parsed = runCatching {
                    json.decodeFromString(ApiStatusResponse.serializer(), text)
                }.getOrNull()

                val code = parsed?.statusCode ?: httpStatus.value
                val msg = parsed?.message ?: "[ktor] http ${httpStatus.value}"

                emit(Result.failure(ApiException(code, msg)))
            } catch (t: Throwable) {
                emit(Result.failure(t))
            }
        }
}
