package com.tdd.talktobook.data.mapper.interview

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.request.interview.InterviewConversationRequestDto
import com.tdd.talktobook.domain.entity.request.interview.InterviewConversationRequestModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object PostInterviewConversationMapper : BaseMapper() {
    fun InterviewConversationRequestModel.toDto() =
        InterviewConversationRequestDto(
            conversations =
                conversation.map { item ->
                    InterviewConversationRequestDto.InterviewConversation(
                        content = item.content,
                        conversationType = item.chatType.content,
                    )
                },
        )

    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<Boolean>> {
        return baseMapper<Nothing, Boolean>(
            apiCall = { apiCall() },
            successDeserializer = null,
            responseToModel = { true },
        )
    }
}
