package com.tdd.talktobook.core.ui.common.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.ui.common.item.InterviewListItem
import com.tdd.talktobook.domain.entity.enums.ChatType
import com.tdd.talktobook.domain.entity.response.interview.InterviewChatItem

@Composable
fun InterviewList(
    interviewList: List<InterviewChatItem>,
    modifier: Modifier,
) {
    InterviewListContent(
        interviewList = interviewList,
        modifier = modifier,
    )
}

@Composable
private fun InterviewListContent(
    interviewList: List<InterviewChatItem>,
    modifier: Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        interviewList.forEach { item ->
            InterviewListItem(
                chatType = item.chatType,
                chatText = item.content,
                modifier =
                    Modifier
                        .align(if (item.chatType == ChatType.BOT) Alignment.Start else Alignment.End),
            )
        }
    }
}
