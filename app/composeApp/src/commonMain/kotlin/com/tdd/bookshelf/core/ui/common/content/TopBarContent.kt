package com.tdd.bookshelf.core.ui.common.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bookshelf.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Neutral900
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun TopBarContent(
    content: String,
    onClickIcon: () -> Unit = {},
    interactionSource: MutableInteractionSource,
    iconVisible: Boolean = true,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Text(
            text = content,
            style = BookShelfTypo.SemiBold,
            color = Neutral900,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(vertical = 30.dp),
        )

        if (iconVisible) {
            AsyncImage(
                model = Res.getUri("files/ic_back.svg"),
                contentDescription = "",
                modifier =
                    Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .size(24.dp)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onClickIcon,
                        ),
            )
        }
    }
}
