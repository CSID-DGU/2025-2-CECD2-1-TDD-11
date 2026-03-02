package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.interview.ai.ChatInterviewRequestModel
import com.tdd.talktobook.domain.entity.request.interview.ai.StartInterviewRequestModel
import com.tdd.talktobook.domain.entity.response.interview.ai.ChatInterviewResponseModel
import com.tdd.talktobook.domain.entity.response.interview.ai.StartInterviewResponseModel
import kotlinx.coroutines.flow.Flow

interface InterviewAIRepository {
    suspend fun postStartInterview(body: StartInterviewRequestModel): Flow<Result<StartInterviewResponseModel>>

    suspend fun postChatInterview(body: ChatInterviewRequestModel): Flow<Result<ChatInterviewResponseModel>>
}
