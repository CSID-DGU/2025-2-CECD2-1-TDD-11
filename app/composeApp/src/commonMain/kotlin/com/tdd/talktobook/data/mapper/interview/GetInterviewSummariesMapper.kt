package com.tdd.talktobook.data.mapper.interview

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.interview.InterviewSummariesResponseDto
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesItemModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetInterviewSummariesMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<InterviewSummariesListModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = InterviewSummariesResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    InterviewSummariesListModel(
                        interviews =
                            data.interviews.map { interview ->
                                InterviewSummariesItemModel(
                                    id = interview.id,
                                    totalMessageCount = interview.totalMessageCount,
                                    summary = interview.summary ?: "",
                                    totalAnswerCount = interview.totalAnswerCount,
                                    date = interview.date,
                                )
                            },
                    )
                } ?: InterviewSummariesListModel()
            },
        )
    }
}
