package com.tdd.talktobook.data.mapper.interview.ai

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.request.interview.ai.StartInterviewRequestDto
import com.tdd.talktobook.data.entity.response.interview.ai.StartInterviewResponseDto
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.ai.StartInterviewResponseModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object StartInterviewMapper : BaseMapper() {
    fun StartInterviewRequestModel.toDto() =
        StartInterviewRequestDto(
            preferredCategories = preferredCategories,
        )

    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<StartInterviewResponseModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = StartInterviewResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    StartInterviewResponseModel(
                        id = data.firstQuestion.id,
                        material = data.firstQuestion.material,
                        materialId = data.firstQuestion.materialId,
                        text = data.firstQuestion.text,
                        type = data.firstQuestion.type,
                    )
                } ?: StartInterviewResponseModel()
            },
        )
    }
}
