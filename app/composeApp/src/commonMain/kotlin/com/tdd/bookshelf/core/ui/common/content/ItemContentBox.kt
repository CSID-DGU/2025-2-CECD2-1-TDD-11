package com.tdd.bookshelf.core.ui.common.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.BackGround1
import com.tdd.bookshelf.core.designsystem.Gray1

@Composable
fun ItemContentBox(
    modifier: Modifier,
    paddingTop: Int = 0,
    paddingBottom: Int = 0,
    paddingStart: Int = 0,
    paddingEnd: Int = 0,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = paddingTop.dp, bottom = paddingBottom.dp, start = paddingStart.dp, end = paddingEnd.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(BackGround1)
            .border(1.dp, Gray1, RoundedCornerShape(5.dp))
    ) {
        content()
    }
}