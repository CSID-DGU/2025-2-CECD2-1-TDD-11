package com.tdd.bookshelf.data.repositoryImpl

import com.tdd.bookshelf.data.dataSource.InterviewDataSource
import com.tdd.bookshelf.data.mapper.base.DefaultBooleanMapper
import com.tdd.bookshelf.data.mapper.interview.GetInterviewConversationMapper
import com.tdd.bookshelf.data.mapper.interview.GetInterviewQuestionListMapper
import com.tdd.bookshelf.data.mapper.interview.PostInterviewConversationMapper
import com.tdd.bookshelf.data.mapper.interview.PostInterviewConversationMapper.toDto
import com.tdd.bookshelf.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.bookshelf.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [InterviewRepository::class])
class InterviewRepositoryImpl(
    private val interviewDataSource: InterviewDataSource,
) : InterviewRepository {
    override suspend fun getInterviewConversation(interviewId: Int): Flow<Result<InterviewConversationListModel>> =
        GetInterviewConversationMapper.responseToModel(apiCall = {
            interviewDataSource.getInterviewConversation(interviewId)
        })

    override suspend fun postInterviewRenewal(interviewId: Int): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            interviewDataSource.postInterviewRenewal(interviewId)
        })

    override suspend fun postInterviewConversation(request: InterviewConversationRequestModel): Flow<Result<Boolean>> =
        PostInterviewConversationMapper.responseToModel(apiCall = {
            interviewDataSource.postInterviewConversation(request.interviewId, request.toDto())
        })

    override suspend fun getInterviewQuestionList(interviewId: Int): Flow<Result<InterviewQuestionListModel>> =
        GetInterviewQuestionListMapper.responseToModel(apiCall = {
            interviewDataSource.getInterviewQuestion(interviewId)
        })
}
