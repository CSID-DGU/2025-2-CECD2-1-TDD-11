package com.tdd.talktobook.core.ui.common.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray2
import com.tdd.talktobook.core.designsystem.Gray3
import com.tdd.talktobook.core.designsystem.Main1
import com.tdd.talktobook.core.designsystem.Red1
import com.tdd.talktobook.core.designsystem.White2

@Composable
fun BasicTextFieldBoxContent(
    textInput: String = "",
    onValueChange: (String) -> Unit = {},
    hintText: String = "",
    errorText: String = "",
    isError: Boolean = false,
    maxTextNum: Int = 0,
    isTextNumVisible: Boolean = false,
    modifier: Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    var borderColor = Gray2

    if (isError) {
        borderColor = Red1
    } else if (textInput.isEmpty() && !isFocused) {
        borderColor = Gray2
    } else if (textInput.isNotEmpty() && !isFocused) {
        borderColor = Black1
    } else if (isFocused) {
        borderColor = Main1
    }

    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            focusManager.clearFocus()
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(1.dp, borderColor, RoundedCornerShape(5.dp))
                .background(White2),
    ) {
        BasicTextField(
            value = textInput,
            onValueChange = { input ->
                onValueChange(input)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 18.dp)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
            textStyle =
                BookShelfTypo.Body1.copy(
                    color = Black1,
                ),
            keyboardOptions =
                KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    },
                ),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    if (textInput.isEmpty() && !isFocused) {
                        Text(
                            text = hintText,
                            style = BookShelfTypo.Body3,
                            color = Gray3,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }

    if (isError) {
        Text(
            text = errorText,
            color = Red1,
            style = BookShelfTypo.Body3,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp),
        )
    }

    if (isTextNumVisible) {
        Text(
            text = "${textInput.length}/$maxTextNum",
            color = Gray3,
            style = BookShelfTypo.Caption2,
            textAlign = TextAlign.End,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 10.dp),
        )
    }
}
