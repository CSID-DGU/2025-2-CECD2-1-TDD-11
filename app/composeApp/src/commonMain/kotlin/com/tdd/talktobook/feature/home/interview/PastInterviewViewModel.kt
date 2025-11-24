package com.tdd.talktobook.feature.home.interview

import androidx.lifecycle.viewModelScope
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewConversationListModel
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PastInterviewViewModel(
    private val getInterviewConversationUseCase: GetInterviewConversationUseCase,
) : BaseViewModel<PastInterviewPageState>(
    PastInterviewPageState(),
) {

    fun setSelectedData(date: String, interviewId: Int) {
        updateState(
            uiState.value.copy(
                selectedDate = date,
                interviewId = interviewId
            ),
        )

        initGetInterviewList(interviewId)
    }

    private fun initGetInterviewList(interviewId: Int) {
        viewModelScope.launch {
            getInterviewConversationUseCase(interviewId).collect {
                resultResponse(it, ::onSuccessGetInterview)
            }
        }
    }

    private fun onSuccessGetInterview(data: InterviewConversationListModel) {
        updateState(
            uiState.value.copy(
                interviewList = data.results
            )
        )
    }
}
