package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.CreateInterviewChatRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.InterviewQuestionsRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.ai.ChatInterviewResponseModel
import com.tdd.talktobook.domain.entity.response.interview.ai.InterviewQuestionsAIResponseModel
import com.tdd.talktobook.domain.entity.response.interview.ai.StartInterviewResponseModel
import kotlinx.coroutines.flow.Flow

interface InterviewAIRepository {
    suspend fun postInterviewQuestions(body: InterviewQuestionsRequestModel): Flow<Result<InterviewQuestionsAIResponseModel>>

    suspend fun postCreateInterviewChat(body: CreateInterviewChatRequestModel): Flow<Result<String>>

    suspend fun postStartInterview(body: StartInterviewRequestModel): Flow<Result<StartInterviewResponseModel>>

    suspend fun postChatInterview(body: ChatInterviewRequestModel): Flow<Result<ChatInterviewResponseModel>>
}
