package com.tdd.talktobook.feature.home.interview

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.ui.common.content.InterviewList
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PastInterviewScreen(
    goBackToHome: () -> Unit,
    selectedDate: String,
    interviewId: Int,
) {
    val viewModel: PastInterviewViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.setSelectedData(selectedDate, interviewId)
    }

    PastInterviewContent(
        interactionSource = interactionSource,
        interviewList = uiState.interviewList,
        onClickBack = { goBackToHome() },
        selectedDate = uiState.selectedDate,
    )
}

@Composable
private fun PastInterviewContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    interviewList: List<InterviewChatItem> = emptyList(),
    onClickBack: () -> Unit = {},
    selectedDate: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        TopBarContent(
            content = "$selectedDate 대화 내역",
            onClickIcon = onClickBack,
            interactionSource = interactionSource,
            iconVisible = true,
        )

        InterviewList(
            interviewList = interviewList,
            modifier = Modifier.weight(1f),
        )
    }
}
