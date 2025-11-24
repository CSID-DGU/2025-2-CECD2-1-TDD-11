package com.tdd.talktobook.data.mapper.interview.ai

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.interview.ai.ChatInterviewResponseDto
import com.tdd.talktobook.domain.entity.response.interview.ai.ChatInterviewResponseModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object ChatInterviewMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<ChatInterviewResponseModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = ChatInterviewResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    ChatInterviewResponseModel(
                        lastAnswerMaterialsId = data.lastAnswerMaterialsId,
                        id = data.nextQuestion.id,
                        material = data.nextQuestion.material,
                        materialId = data.nextQuestion.materialId,
                        text = data.nextQuestion.text,
                        type = data.nextQuestion.type,
                    )
                } ?: ChatInterviewResponseModel()
            },
        )
    }
}
