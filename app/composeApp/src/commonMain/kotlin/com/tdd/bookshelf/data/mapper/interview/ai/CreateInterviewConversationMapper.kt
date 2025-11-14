package com.tdd.bookshelf.data.mapper.interview.ai

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.request.interview.ai.ChapterInfoDto
import com.tdd.bookshelf.data.entity.request.interview.ai.CreateInterviewChatRequestDto
import com.tdd.bookshelf.data.entity.request.interview.ai.UserInfoDto
import com.tdd.bookshelf.data.entity.response.interview.InterviewConversationDto
import com.tdd.bookshelf.domain.entity.request.interview.ai.CreateInterviewChatRequestModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.serializer

object CreateInterviewConversationMapper : BaseMapper() {
    fun CreateInterviewChatRequestModel.toDto() =
        CreateInterviewChatRequestDto(
            userInfo =
                UserInfoDto(
                    userInfo.name,
                    userInfo.bornedAt,
                    userInfo.gender,
                    userInfo.hasChildren,
                    userInfo.occupation ?: "",
                    userInfo.educationLevel ?: "",
                    userInfo.maritalStatus ?: "",
                ),
            chapterInfo =
                ChapterInfoDto(
                    chapterInfo.title,
                    chapterInfo.description,
                ),
            subChapterInfo =
                ChapterInfoDto(
                    chapterInfo.title,
                    chapterInfo.description,
                ),
            conversationHistory =
                conversationHistory.map { item ->
                    InterviewConversationDto(
                        content = item.content,
                        conversationType = item.chatType.content,
                    )
                },
            currentAnswer = currentAnswer,
            questionLimit = questionLimit,
        )

    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<String>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = String.serializer(),
            responseToModel = { response ->
                response ?: ""
            },
        )
    }
}
