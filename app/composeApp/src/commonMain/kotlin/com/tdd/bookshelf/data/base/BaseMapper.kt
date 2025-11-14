package com.tdd.bookshelf.data.base

import com.tdd.bookshelf.data.entity.response.api.ApiError
import com.tdd.bookshelf.data.entity.response.api.ApiException
import com.tdd.bookshelf.data.entity.response.api.ApiStatusResponse
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
            val response = apiCall()
            val defaultModel = responseToModel(null)

            try {
                val httpStatus = response?.status
                val responseBody = response?.bodyAsText() ?: ""

                if (httpStatus == HttpStatusCode.OK) {
//                val dto = json.decodeFromString(successDeserializer, responseBody)
//                val model = responseToModel(dto)

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
                        json.decodeFromString(
                            ApiStatusResponse.serializer(),
                            responseBody,
                        )
                    val msg = error.message ?: "[ktor] http error ${httpStatus?.value ?: "Unknown"}"
                    val code = error.statusCode ?: httpStatus?.value ?: 0
                    emit(Result.failure(ApiException(code, "[ktor] $code: $msg")))
                }
            } catch (e: ResponseException) {
                val code = e.response.status.value
                val text = e.response.bodyAsText()
                val msg =
                    runCatching { json.decodeFromString(ApiError.serializer(), text).message }
                        .getOrNull() ?: "[ktor] http $code"
                emit(Result.failure(ApiException(code, msg)))
            } catch (t: Throwable) {
                emit(Result.failure(t))
            }
        }
}
