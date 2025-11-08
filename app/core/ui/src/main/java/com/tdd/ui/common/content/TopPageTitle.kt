package com.tdd.ui.common.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Main3

@Composable
fun TopPageTitle(
    title: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Main3,
            style = BookShelfTypo.head20,
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 50.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTopPageTitle() {
    TopPageTitle("인터뷰")
}