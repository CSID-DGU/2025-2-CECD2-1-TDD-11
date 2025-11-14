package com.tdd.interview.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.design_system.BackGround
import com.tdd.design_system.InterviewTitle
import com.tdd.domain.entity.response.interview.InterviewChattingModel.Chatting
import com.tdd.ui.common.content.InterviewChatting
import com.tdd.ui.common.content.TopPageTitle

@Composable
fun InterviewMainScreen() {

    val viewModel: InterviewMainViewModel = hiltViewModel()
    val uiState: InterviewMainPageState by viewModel.uiState.collectAsStateWithLifecycle()

    InterviewMainContent(
        chattingList = uiState.chattingList
    )
}

@Composable
fun InterviewMainContent(
    chattingList: List<Chatting> = emptyList(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround),
    ) {
        TopPageTitle(
            title = InterviewTitle
        )

        InterviewChatting(
            chattingList = chattingList,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInterviewMain() {
    InterviewMainContent()
}