package com.tdd.bookshelf.data.mapper.interview

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.interview.InterviewConversationResponseDto
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import com.tdd.bookshelf.domain.entity.response.interview.InterviewConversationListModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetInterviewConversationMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<InterviewConversationListModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = InterviewConversationResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    InterviewConversationListModel(
                        results =
                            data.results.map { result ->
                                InterviewChatItem(
                                    content = result.content,
                                    chatType = ChatType.getType(result.conversationType),
                                )
                            },
                        currentPage = data.currentPage,
                        totalElements = data.totalElements,
                        totalPages = data.totalPages,
                        hasNextPage = data.hasNextPage,
                        hasPreviousPage = data.hasPreviousPage,
                    )
                } ?: InterviewConversationListModel()
            },
        )
    }
}
