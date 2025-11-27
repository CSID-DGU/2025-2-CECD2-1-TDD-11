package com.tdd.talktobook.core.ui.common.textfield

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldBox(
    textInput: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    errorText: String = "",
    isError: Boolean = false,
) {
    BasicTextFieldBoxContent(
        textInput = textInput,
        onValueChange = onValueChange,
        hintText = hintText,
        errorText = errorText,
        isError = isError,
        modifier =
            Modifier
                .height(55.dp),
    )
}
