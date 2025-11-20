package com.tdd.bookshelf.core.ui.common.content

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Main1

@Composable
fun SeriesNumText(
    totalNum: Int,
    currentNum: Int,
) {
    Text(
        text = "$currentNum/$totalNum",
        color = Main1,
        style = BookShelfTypo.Head3,
        modifier =
            Modifier
                .padding(start = 20.dp),
    )
}
