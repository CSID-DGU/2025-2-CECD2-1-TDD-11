package com.tdd.ui.common.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray5
import com.tdd.design_system.Main3

@Composable
fun TopTitleContent(
    title: String,
    semiTitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = BookShelfTypo.head20,
            color = Main3,
            modifier = Modifier
                .padding(top = 70.dp)
        )

        Text(
            text = semiTitle,
            style = BookShelfTypo.head30,
            textAlign = TextAlign.Center,
            color = Gray5,
            modifier = Modifier
                .padding(top = 30.dp, start = 70.dp, end = 70.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTopTitle() {
    TopTitleContent("", "")
}