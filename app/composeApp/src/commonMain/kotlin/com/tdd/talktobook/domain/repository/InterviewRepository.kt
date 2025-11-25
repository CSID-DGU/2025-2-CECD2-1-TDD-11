package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import kotlinx.coroutines.flow.Flow

interface InterviewRepository {
    suspend fun getInterviewConversation(interviewId: Int): Flow<Result<InterviewConversationListModel>>

    suspend fun postInterviewRenewal(interviewId: Int): Flow<Result<Boolean>>

    suspend fun postInterviewConversation(request: InterviewConversationRequestModel): Flow<Result<Boolean>>

    suspend fun getInterviewQuestionList(interviewId: Int): Flow<Result<InterviewQuestionListModel>>

    suspend fun getInterviewSummaries(request: InterviewSummariesRequestModel): Flow<Result<InterviewSummariesListModel>>

    suspend fun saveInterviewId(request: Int): Flow<Result<Boolean>>

    suspend fun getInterviewId(): Flow<Result<Int>>

    suspend fun clearToken(): Flow<Result<Boolean>>

    suspend fun clearAllData(): Flow<Result<Boolean>>
}
