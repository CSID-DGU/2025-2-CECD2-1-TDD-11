package com.tdd.talktobook.core.ui.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun SelectRectangleItem(
    modifier: Modifier,
    itemText: String,
    isItemSelected: Boolean = false,
    onSelectItem: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    SelectRectangleItemContent(
        interactionSource = interactionSource,
        modifier = modifier,
        itemText = itemText,
        isItemSelected = isItemSelected,
        onSelectItem = onSelectItem
    )
}


@Composable
private fun SelectRectangleItemContent(
    interactionSource: MutableInteractionSource,
    modifier: Modifier,
    itemText: String,
    isItemSelected: Boolean,
    onSelectItem: () -> Unit
) {
    Box(
        modifier = modifier.run {
            fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(color = if (isItemSelected) Main1 else Gray1, RoundedCornerShape(5.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onSelectItem
                )
        }
    ) {
        Text(
            text = itemText,
            color = if (isItemSelected) White3 else Gray4,
            style = BookShelfTypo.Body3,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 13.dp)
        )
    }
}