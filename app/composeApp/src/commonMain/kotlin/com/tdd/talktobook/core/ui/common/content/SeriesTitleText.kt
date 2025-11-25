package com.tdd.talktobook.core.ui.common.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo

@Composable
fun SeriesTitleText(
    currentTitle: String,
    paddingTop: Int,
) {
    Text(
        text = currentTitle,
        color = Black1,
        style = BookShelfTypo.Head3,
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = paddingTop.dp),
    )
}
