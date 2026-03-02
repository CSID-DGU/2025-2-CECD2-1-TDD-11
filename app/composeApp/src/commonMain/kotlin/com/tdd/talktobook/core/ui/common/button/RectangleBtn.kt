package com.tdd.talktobook.core.ui.common.button

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
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray1
import com.tdd.talktobook.core.designsystem.Gray4
import com.tdd.talktobook.core.designsystem.Main1
import com.tdd.talktobook.core.designsystem.White3

@Composable
fun RectangleBtn(
    btnContent: String,
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
) {
    val interactionSource = remember { MutableInteractionSource() }

    RectangleBtnContent(
        interactionSource = interactionSource,
        btnTextContent = btnContent,
        isBtnActivated = isBtnActivated,
        onClickAction = onClickAction,
        modifier = modifier,
    )
}

@Composable
private fun RectangleBtnContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    btnTextContent: String,
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(10.dp))
                .background(if (isBtnActivated) Main1 else Gray1)
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
            style = BookShelfTypo.Body1,
            color = if (isBtnActivated) White3 else Gray4,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 17.dp),
        )
    }
}
