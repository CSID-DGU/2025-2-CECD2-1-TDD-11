package com.tdd.bookshelf.core.ui.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import com.tdd.bookshelf.domain.entity.enums.ChatType

@Composable
fun InterviewListItem(
    chatText: String,
    chatType: ChatType,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = if (chatType == ChatType.BOT) 0.dp else 5.dp, bottomEnd = if (chatType == ChatType.BOT) 5.dp else 0.dp))
            .background(if (chatType == ChatType.BOT) Gray1 else Main1)
    ) {
        Text(
            text = chatText,
            color = if (chatType == ChatType.BOT) Black1 else White3,
            style = BookShelfTypo.Body2,
            modifier = Modifier
                .padding(vertical = 18.dp, horizontal = 20.dp)
        )
    }
}