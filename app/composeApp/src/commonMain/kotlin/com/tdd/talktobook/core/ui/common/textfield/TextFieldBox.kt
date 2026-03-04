package com.tdd.talktobook.core.ui.common.textfield

import androidx.compose.foundation.layout.height
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
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    passwordIconPath: String = "",
    onClickPasswordIcon: () -> Unit = {}
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
        isPassword = isPassword,
        isPasswordVisible = isPasswordVisible,
        passwordIconPath = passwordIconPath,
        onClickPasswordIcon = onClickPasswordIcon
    )
}
