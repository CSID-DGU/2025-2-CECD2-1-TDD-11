package com.tdd.talktobook.core.ui.common.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tdd.talktobook.core.designsystem.Gray1

@Composable
fun SelectedBottomSheet() {
    SelectedBottomSheetContent()
}

@Composable
private fun SelectedBottomSheetContent() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Gray1),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //
    }
}