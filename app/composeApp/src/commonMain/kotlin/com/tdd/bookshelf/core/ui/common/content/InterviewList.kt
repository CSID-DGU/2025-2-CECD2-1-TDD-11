package com.tdd.bookshelf.core.ui.common.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Gray1
import com.tdd.bookshelf.core.designsystem.Main1
import com.tdd.bookshelf.core.designsystem.White3
import com.tdd.bookshelf.core.ui.common.item.InterviewListItem
import com.tdd.bookshelf.domain.entity.enums.ChatType
import com.tdd.bookshelf.domain.entity.response.interview.InterviewChatItem

@Composable
fun InterviewList(
    interviewList: List<InterviewChatItem>,
) {

    InterviewListContent(
        interviewList = interviewList
    )
}

@Composable
fun InterviewListContent(
    interviewList: List<InterviewChatItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        interviewList.forEach { item ->
            InterviewListItem(
                chatType = item.chatType,
                chatText = item.content
            )
        }
    }
}