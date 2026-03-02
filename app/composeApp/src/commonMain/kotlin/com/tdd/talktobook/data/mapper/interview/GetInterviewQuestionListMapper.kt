package com.tdd.talktobook.data.mapper.interview

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.interview.InterviewQuestionListResponseDto
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionItemModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionListModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetInterviewQuestionListMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<InterviewQuestionListModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = InterviewQuestionListResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    InterviewQuestionListModel(
                        currentQuestionId = data.currentQuestionId,
                        results =
                            data.results.map { result ->
                                InterviewQuestionItemModel(
                                    questionId = result.questionId,
                                    order = result.order,
                                    questionText = result.questionText,
                                )
                            },
                    )
                } ?: InterviewQuestionListModel()
            },
        )
    }
}
