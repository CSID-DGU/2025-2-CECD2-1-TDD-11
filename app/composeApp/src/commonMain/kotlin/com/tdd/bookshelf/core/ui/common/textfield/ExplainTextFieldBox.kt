package com.tdd.bookshelf.core.ui.common.textfield

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExplainTextFieldBox(
    textInput: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    errorText: String = "",
    isError: Boolean = false,
    maxTextNum: Int,
    isTextNumVisible: Boolean,
) {
    BasicTextFieldBoxContent(
        textInput = textInput,
        onValueChange = onValueChange,
        hintText = hintText,
        errorText = errorText,
        isError = isError,
        maxTextNum = maxTextNum,
        isTextNumVisible = isTextNumVisible,
        modifier = Modifier
            .height(260.dp)
    )
}