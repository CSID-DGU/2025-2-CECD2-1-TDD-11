package com.tdd.talktobook.core.ui.common.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.BookShelfTypo

@Composable
fun UnderLineTextBtn(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    textContent: String,
    textColor: Color,
    onClick: () -> Unit = {},
    paddingEnd: Int,
    clickEnabled: Boolean = true,
    modifier: Modifier,
) {
    Text(
        text = textContent,
        color = textColor,
        style =
            BookShelfTypo.Body2.copy(
                textDecoration = TextDecoration.Underline,
            ),
        modifier =
            modifier
                .padding(end = paddingEnd.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                    enabled = clickEnabled,
                ),
    )
}
