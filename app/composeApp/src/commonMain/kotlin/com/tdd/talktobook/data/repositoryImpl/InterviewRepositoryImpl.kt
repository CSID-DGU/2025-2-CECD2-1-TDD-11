package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.InterviewDataSource
import com.tdd.talktobook.data.dataStore.LocalDataStore
import com.tdd.talktobook.data.mapper.base.DefaultBooleanMapper
import com.tdd.talktobook.data.mapper.interview.GetInterviewConversationMapper
import com.tdd.talktobook.data.mapper.interview.GetInterviewQuestionListMapper
import com.tdd.talktobook.data.mapper.interview.GetInterviewSummariesMapper
import com.tdd.talktobook.data.mapper.interview.PostInterviewConversationMapper
import com.tdd.talktobook.data.mapper.interview.PostInterviewConversationMapper.toDto
import com.tdd.talktobook.domain.entity.request.interview.InterviewConversationRequestModel
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import com.tdd.talktobook.domain.repository.InterviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single(binds = [InterviewRepository::class])
class InterviewRepositoryImpl(
    private val interviewDataSource: InterviewDataSource,
    private val localDataStore: LocalDataStore,
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

    override suspend fun getInterviewSummaries(request: InterviewSummariesRequestModel): Flow<Result<InterviewSummariesListModel>> =
        GetInterviewSummariesMapper.responseToModel(apiCall = {
            interviewDataSource.getInterviewSummaries(request.autobiographyId, request.year, request.month)
        })

    override suspend fun saveInterviewId(request: Int): Flow<Result<Boolean>> = flow {
        localDataStore.saveCurrentInterviewId(request)
    }

    override suspend fun getInterviewId(): Flow<Result<Int>> = flow {
        localDataStore.currentInterviewId.collect { id ->
            if (id != null) {
                emit(Result.success(id))
            } else {
                emit(Result.failure(Exception("[dataStore] interviewId is null")))
            }
        }
    }
}
