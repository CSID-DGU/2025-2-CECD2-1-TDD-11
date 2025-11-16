package com.tdd.bookshelf.domain.repository

import com.tdd.bookshelf.domain.entity.request.interview.ai.CreateInterviewChatRequestModel
import com.tdd.bookshelf.domain.entity.request.interview.ai.InterviewQuestionsRequestModel
import com.tdd.bookshelf.domain.entity.response.interview.ai.InterviewQuestionsAIResponseModel
import kotlinx.coroutines.flow.Flow

interface InterviewAIRepository {
    suspend fun postInterviewQuestions(body: InterviewQuestionsRequestModel): Flow<Result<InterviewQuestionsAIResponseModel>>

    suspend fun postCreateInterviewChat(body: CreateInterviewChatRequestModel): Flow<Result<String>>
}
