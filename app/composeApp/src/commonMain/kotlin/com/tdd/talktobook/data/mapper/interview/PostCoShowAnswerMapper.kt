package com.tdd.talktobook.data.mapper.interview

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.interview.CoShowAnswerResponseDto
import com.tdd.talktobook.domain.entity.response.interview.CoShowAnswerModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object PostCoShowAnswerMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<CoShowAnswerModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = CoShowAnswerResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    CoShowAnswerModel(
                        id = data.id,
                        order = data.order,
                        question = data.question,
                        isLast = data.isLast,
                    )
                } ?: CoShowAnswerModel()
            },
        )
    }
}
