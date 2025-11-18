package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionListModel
import kotlinx.coroutines.flow.Flow

interface InterviewRepository {
    suspend fun getInterviewConversation(interviewId: Int): Flow<Result<InterviewConversationListModel>>

    suspend fun postInterviewRenewal(interviewId: Int): Flow<Result<Boolean>>

    suspend fun postInterviewConversation(request: InterviewConversationRequestModel): Flow<Result<Boolean>>

    suspend fun getInterviewQuestionList(interviewId: Int): Flow<Result<InterviewQuestionListModel>>
}
