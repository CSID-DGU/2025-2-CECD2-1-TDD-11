package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.CurrentInterviewProgressResponseDto
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetCurrentInterviewProgressMapper: BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<CurrentInterviewProgressModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = CurrentInterviewProgressResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    CurrentInterviewProgressModel(
                        progressPercentage = data.progressPercentage,
                        status = AutobiographyStatusType.getType(data.status)
                    )
                }?: CurrentInterviewProgressModel()
            }
        )
    }
}