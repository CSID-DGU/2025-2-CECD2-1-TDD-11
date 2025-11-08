package com.tdd.ui.common.item

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray3
import com.tdd.design_system.Gray5
import com.tdd.design_system.Main1
import com.tdd.design_system.White3

@Composable
fun SelectItem(
    itemText: String,
    modifier: Modifier,
    isItemSelected: Boolean = false,
    onSelectItem: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    SelectItemContent(
        modifier = modifier,
        interactionSource = interactionSource,
        itemText = itemText,
        isItemSelected = isItemSelected,
        onSelectItem = onSelectItem
    )
}

@Composable
fun SelectItemContent(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    itemText: String = "",
    isItemSelected: Boolean = false,
    onSelectItem: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isItemSelected) Main1 else Gray3, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelectItem
            )
    ) {
        Text(
            text = itemText,
            color = if (isItemSelected) White3 else Gray5,
            style = BookShelfTypo.head30,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 15.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewSelectItem() {
    SelectItemContent()
}