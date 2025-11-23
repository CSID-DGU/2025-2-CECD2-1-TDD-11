package com.tdd.talktobook.core.ui.common.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.BackGround1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray1
import com.tdd.talktobook.core.designsystem.Gray4
import com.tdd.talktobook.core.designsystem.Main1
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import talktobook.composeapp.generated.resources.Res
import talktobook.composeapp.generated.resources.img_chapter_detail

@Composable
fun SelectCircleListItem(
    itemImg: DrawableResource = Res.drawable.img_chapter_detail,
    itemText: String,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    SelectCircleListItemContent(
        interactionSource = interactionSource,
        itemImg = itemImg,
        itemText = itemText,
        isSelected = isSelected,
        onSelect = onSelect,
    )
}

@Composable
fun SelectCircleListItemContent(
    interactionSource: MutableInteractionSource,
    itemImg: DrawableResource,
    itemText: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onSelect,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(1.dp, if (isSelected) Main1 else Gray1, CircleShape)
                    .background(BackGround1),
        ) {
            Image(
                painter = painterResource(itemImg),
                contentDescription = "list item",
                modifier =
                    Modifier
                        .size(50.dp)
                        .align(Alignment.Center),
            )
        }

        Text(
            text = itemText,
            color = if (isSelected) Main1 else Gray4,
            style = BookShelfTypo.Caption4,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .padding(top = 5.dp),
        )
    }
}
