package com.tdd.bookshelf.feature.home.interview

import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PastInterviewViewModel(): BaseViewModel<PastInterviewPageState>(
    PastInterviewPageState()
) {

    init {
        initSetInterviewList()
    }

    private fun initSetInterviewList() {
        val interviews: List<InterviewChatItem> = listOf(
            InterviewChatItem("나는 AI", ChatType.BOT),
            InterviewChatItem("나는 인간", ChatType.HUMAN),
        )

        updateState(
            uiState.value.copy(
                interviewList = interviews
            )
        )
    }

    fun setSelectedDate(date: String) {
        updateState(
            uiState.value.copy(
                selectedDate = date
            )
        )
    }
}