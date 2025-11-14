package com.tdd.interview.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.design_system.BackGround
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.InterviewTitle
import com.tdd.design_system.Main3
import com.tdd.design_system.R
import com.tdd.domain.entity.response.interview.InterviewChattingModel.Chatting
import com.tdd.ui.common.content.InterviewChatting

@Composable
fun InterviewScreen(
    goHomePage: () -> Unit
) {

    val viewModel: InterviewViewModel = hiltViewModel()
    val uiState: InterviewPageState by viewModel.uiState.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    InterviewContent(
        onClickHomeBtn = { goHomePage() },
        interactionSource = interactionSource,
        chattingList = uiState.chattingList
    )
}

@Composable
fun InterviewContent(
    onClickHomeBtn: () -> Unit = {},
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    chattingList: List<Chatting> = emptyList(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = InterviewTitle,
                color = Main3,
                style = BookShelfTypo.head20,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 50.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_home),
                contentDescription = "home",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 50.dp)
                    .size(40.dp)
                    .clickable(
                        onClick = onClickHomeBtn,
                        interactionSource = interactionSource,
                        indication = null
                    )
            )
        }

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
fun PreviewInterview() {
    InterviewContent()
}