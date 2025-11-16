package com.tdd.bookshelf.feature.interview

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import com.tdd.bookshelf.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionItemModel
import com.tdd.bookshelf.domain.entity.response.interview.InterviewQuestionListModel
import com.tdd.bookshelf.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.bookshelf.domain.usecase.interview.GetInterviewQuestionListUseCase
import com.tdd.bookshelf.feature.interview.type.ConversationType
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class InterviewViewModel(
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
    private val getInterviewQuestionListUseCase: GetInterviewQuestionListUseCase,
) : BaseViewModel<InterviewPageState>(
        InterviewPageState(),
    ) {
    fun setInterview(interviewId: Int) {
        d("[test] interviewViewModel -> $interviewId")
        updateState(
            uiState.value.copy(
                interviewId = interviewId,
            ),
        )

        initSetInterviewList(interviewId)
//        initSetInterviewQuestion(interviewId)
    }

    private fun initSetInterviewList(interviewId: Int) {
        viewModelScope.launch {
            getInterviewConversationUseCase(interviewId).collect {
                resultResponse(it, { data -> onSuccessSetInterviewConversationList(data, interviewId) })
            }
        }
    }

    private fun onSuccessSetInterviewConversationList(
        data: InterviewConversationListModel,
        interviewId: Int,
    ) {
        d("[test] interview chats: -> ${data.results}")
        updateState(
            uiState.value.copy(
                interviewConversationModel = data,
                interviewChatList = data.results,
            ),
        )

        initSetInterviewQuestion(interviewId)
    }

    private fun initSetInterviewQuestion(interviewId: Int) {
        viewModelScope.launch {
            getInterviewQuestionListUseCase(interviewId).collect {
                resultResponse(it, ::onSuccessSetInterviewQuestion)
            }
        }
    }

    private fun onSuccessSetInterviewQuestion(data: InterviewQuestionListModel) {
        d("[test] interview -> questions: ${data.results}, currentId: ${data.currentQuestionId}")
        updateState(
            uiState.value.copy(
                interviewCurrentQuestionId = data.currentQuestionId,
                interviewQuestionList = data.results,
            ),
        )

        addInterviewConversation(data.results, data.currentQuestionId)
    }

    private fun addInterviewConversation(
        questions: List<InterviewQuestionItemModel>,
        currentQuestionId: Int,
    ) {
        val currentQuestion: InterviewQuestionItemModel = questions.firstOrNull { it.questionId == currentQuestionId } ?: InterviewQuestionItemModel()
        val currentConversationModel = InterviewChatItem(content = currentQuestion.questionText, chatType = ChatType.BOT)
        val updatedChatList = uiState.value.interviewChatList + currentConversationModel

        updateState(
            uiState.value.copy(
                interviewChatList = updatedChatList,
            ),
        )
    }

    fun beginInterview() {
        updateState(
            uiState.value.copy(
                interviewProgressType = ConversationType.ING,
            ),
        )
    }

    fun setInterviewAnswer(chat: String) {
        val originalInterviews = uiState.value.interviewChatList
        val newAnswer = InterviewChatItem(content = chat, chatType = ChatType.HUMAN)

        updateState(
            uiState.value.copy(
                interviewChatList = originalInterviews + newAnswer,
                interviewProgressType = ConversationType.BEFORE,
            ),
        )
    }
}
