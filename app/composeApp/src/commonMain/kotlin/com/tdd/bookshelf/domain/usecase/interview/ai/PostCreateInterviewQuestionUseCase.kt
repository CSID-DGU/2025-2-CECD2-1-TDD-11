package com.tdd.bookshelf.domain.usecase.interview.ai

import com.tdd.bookshelf.domain.base.UseCase
import com.tdd.bookshelf.domain.entity.request.interview.ai.InterviewQuestionsRequestModel
import com.tdd.bookshelf.domain.entity.response.interview.ai.InterviewQuestionsAIResponseModel
import com.tdd.bookshelf.domain.repository.InterviewAIRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
class PostCreateInterviewQuestionUseCase(
    private val repository: InterviewAIRepository,
) : UseCase<InterviewQuestionsRequestModel, Result<InterviewQuestionsAIResponseModel>>() {
    override suspend fun invoke(request: InterviewQuestionsRequestModel): Flow<Result<InterviewQuestionsAIResponseModel>> =
        repository.postInterviewQuestions(request)
}
