package com.tdd.bookshelf.data.mapper.interview

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.interview.InterviewQuestionListResponseDto
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionItemModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionListModel
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
