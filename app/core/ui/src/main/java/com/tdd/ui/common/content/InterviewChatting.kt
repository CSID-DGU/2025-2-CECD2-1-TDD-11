package com.tdd.ui.common.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BackGround
import com.tdd.design_system.Black1
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray1
import com.tdd.design_system.Main3
import com.tdd.design_system.White2
import com.tdd.domain.entity.enum.ChattingType
import com.tdd.domain.entity.response.interview.InterviewChattingModel.Chatting
import com.tdd.design_system.R

@Composable
fun InterviewChatting(
    chattingList: List<Chatting>,
    modifier: Modifier,
) {
    InterviewChattingContent(
        chattingList = chattingList,
        modifier = modifier
    )
}

@Composable
fun InterviewChattingContent(
    chattingList: List<Chatting> = emptyList(),
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackGround)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_interview_background),
            contentDescription = "background",
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.Center)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp, vertical = 30.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chattingList) { chatting ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (chatting.type == ChattingType.MIRROR) {
                        Arrangement.Start
                    } else {
                        Arrangement.End
                    }
                ) {
                    if (chatting.type == ChattingType.MIRROR) {
                        InterviewChattingMirrorType(
                            chatting = chatting,
                        )
                    } else {
                        InterviewChattingHumanType(
                            chatting = chatting,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InterviewChattingMirrorType(
    chatting: Chatting,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp))
            .background(Gray1)
            .wrapContentSize()
    ) {
        Text(
            text = chatting.content,
            color = Black1,
            style = BookShelfTypo.body20,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 15.dp)
        )
    }
}

@Composable
fun InterviewChattingHumanType(
    chatting: Chatting,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp))
            .background(Main3)
            .wrapContentSize()
    ) {
        Text(
            text = chatting.content,
            color = White2,
            style = BookShelfTypo.body20,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 15.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInterviewChatting() {
    InterviewChattingContent()
}