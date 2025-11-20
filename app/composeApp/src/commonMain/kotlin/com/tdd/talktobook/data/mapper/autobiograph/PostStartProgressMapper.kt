package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.StartProgressResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object PostStartProgressMapper: BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<InterviewAutobiographyModel>> {
        return  baseMapper(
            apiCall = { apiCall() },
            successDeserializer = StartProgressResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    InterviewAutobiographyModel(
                        interviewId = data.interviewId,
                        autobiographyId = data.autobiographyId
                    )
                } ?: InterviewAutobiographyModel()
            }
        )
    }
}