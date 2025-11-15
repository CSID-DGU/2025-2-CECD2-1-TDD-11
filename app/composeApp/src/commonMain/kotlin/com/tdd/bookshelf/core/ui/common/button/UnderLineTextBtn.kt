package com.tdd.bookshelf.core.ui.common.button

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.BookShelfTypo

@Composable
fun UnderLineTextBtn(
    interactionSource: MutableInteractionSource,
    textContent: String,
    textColor: Color,
    onClick: () -> Unit,
    paddingEnd: Int,
) {
    Text(
        text = textContent,
        color = textColor,
        style = BookShelfTypo.Body2.copy(
            textDecoration = TextDecoration.Underline
        ),
        textAlign = TextAlign.End,
        modifier = Modifier
            .padding(end = paddingEnd.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    )
}