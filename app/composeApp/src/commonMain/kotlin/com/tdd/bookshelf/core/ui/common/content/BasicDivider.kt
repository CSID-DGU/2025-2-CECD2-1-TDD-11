package com.tdd.bookshelf.core.ui.common.content

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.Gray1

@Composable
fun BasicDivider() {

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = Gray1,
        thickness = 2.dp
    )
}