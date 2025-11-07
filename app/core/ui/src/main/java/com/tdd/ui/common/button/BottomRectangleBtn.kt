package com.tdd.ui.common.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray3
import com.tdd.design_system.Gray5
import com.tdd.design_system.Main1
import com.tdd.design_system.White3

@Composable
fun BottomRectangleBtn(
    btnTextContent: String,
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    BottomRectangleBtnContent(
        interactionSource = interactionSource,
        btnTextContent = btnTextContent,
        isBtnActivated = isBtnActivated,
        onClickAction = onClickAction
    )
}

@Composable
fun BottomRectangleBtnContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    btnTextContent: String = "",
    isBtnActivated: Boolean = false,
    onClickAction: () -> Unit = {},
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(bottom = 36.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 25.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (isBtnActivated) Main1 else Gray3)
                .wrapContentHeight()
                .clickable(
                    enabled = isBtnActivated,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { onClickAction() }
                )
        ) {
            Text(
                text = btnTextContent,
                style = BookShelfTypo.body50,
                color = if (isBtnActivated) White3 else Gray5,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 15.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBottomRectangleBtn() {
    BottomRectangleBtnContent()
}