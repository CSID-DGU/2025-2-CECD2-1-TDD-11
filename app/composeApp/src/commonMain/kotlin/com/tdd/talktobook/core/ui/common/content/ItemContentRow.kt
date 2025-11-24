package com.tdd.talktobook.core.ui.common.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import talktobook.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun ItemContentRow(
    iconImgUrl: String,
    content: String,
    isNextVisible: Boolean = true,
    onClickNext: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier =
            Modifier
                .padding(vertical = 14.dp, horizontal = 20.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClickNext,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = Res.getUri(iconImgUrl),
            contentDescription = "icon",
            modifier =
                Modifier
                    .size(19.dp),
        )

        Text(
            text = content,
            color = Black1,
            style = BookShelfTypo.Body1,
            modifier =
                Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
        )

        if (isNextVisible) {
            AsyncImage(
                model = Res.getUri("files/ic_right.svg"),
                contentDescription = "",
                modifier =
                    Modifier
                        .padding(end = 20.dp)
                        .size(24.dp),
            )
        }
    }
}
