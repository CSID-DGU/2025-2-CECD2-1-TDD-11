package com.tdd.bookshelf.core.ui.common.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.Blue300
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Gray200
import com.tdd.bookshelf.core.designsystem.Gray50

@Composable
fun RectangleBtn(
    btnContent: String,
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    RectangleBtnContent(
        interactionSource = interactionSource,
        btnTextContent = btnContent,
        isBtnActivated = isBtnActivated,
        onClickAction = onClickAction,
    )
}

@Composable
fun RectangleBtnContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    btnTextContent: String,
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isBtnActivated) Blue300 else Gray200)
                .wrapContentHeight()
                .clickable(
                    enabled = isBtnActivated,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { onClickAction() },
                ),
    ) {
        Text(
            text = btnTextContent,
            style = BookShelfTypo.Medium,
            color = Gray50,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 17.dp),
        )
    }
}
