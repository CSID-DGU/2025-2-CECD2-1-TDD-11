package com.tdd.talktobook.core.ui.common.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.White2

@Composable
fun DisEnabledTextFieldBox(
    textContent: String,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(1.dp, Black1, RoundedCornerShape(5.dp))
                .background(White2),
    ) {
        Text(
            text = textContent,
            style = BookShelfTypo.Body1,
            color = Black1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 18.dp),
        )
    }
}
