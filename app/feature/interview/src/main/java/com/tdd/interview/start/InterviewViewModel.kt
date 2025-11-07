package com.tdd.interview.start

import androidx.lifecycle.viewModelScope
import com.tdd.domain.entity.enum.ChattingType
import com.tdd.domain.entity.response.interview.InterviewChattingModel
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewViewModel @Inject constructor(

): BaseViewModel<InterviewPageState>(
    InterviewPageState()
) {
    init {
        setInterviewChatting()
    }

    private fun setInterviewChatting() {
        val chattings: List<InterviewChattingModel.Chatting> = listOf(
            InterviewChattingModel.Chatting("어릴 때 좋았던 기억을 알려 주세요.", ChattingType.MIRROR),
            InterviewChattingModel.Chatting("음, 어릴 때 가족끼리 여행을 다니면서 많은 경험을 했던 것 같아", ChattingType.HUMAN),
            InterviewChattingModel.Chatting("그 기억이 어떤 영향을 주었나요?", ChattingType.MIRROR),
            InterviewChattingModel.Chatting("최대한 다양한 것을 경험해보고 시도해보고, 도전해 보는 사람으로 자라게 된 것 같아", ChattingType.HUMAN),
        )

        viewModelScope.launch {
            for (i in 0..3) {
                val subList = chattings.take(i + 1)
                updateChatting(subList)
                delay(if (i % 2 != 0) 1000L else 3000L)
            }
        }
    }

    private fun updateChatting(chatting: List<InterviewChattingModel.Chatting>) {
        updateState(
            uiState.value.copy(
                chattingList = chatting
            )
        )
    }
}