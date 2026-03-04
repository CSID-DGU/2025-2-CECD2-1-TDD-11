package com.tdd.talktobook.core.ui.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.BackGround1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray1
import com.tdd.talktobook.core.designsystem.Gray4
import com.tdd.talktobook.core.designsystem.Main1
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun MaterialListItem(
    itemText: String,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    MaterialListItemContent(
        interactionSource = interactionSource,
        itemText = itemText,
        isSelected = isSelected,
        onSelect = onSelect,
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MaterialListItemContent(
    interactionSource: MutableInteractionSource,
    itemText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .border(2.dp, if (isSelected) Main1 else Gray1, RoundedCornerShape(99.dp))
            .background(BackGround1)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
    ) {
        Text(
            text = itemText,
            color = if (isSelected) Main1 else Gray4,
            style = BookShelfTypo.Body2,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(vertical = 15.dp, horizontal = 20.dp),
        )
    }
}
