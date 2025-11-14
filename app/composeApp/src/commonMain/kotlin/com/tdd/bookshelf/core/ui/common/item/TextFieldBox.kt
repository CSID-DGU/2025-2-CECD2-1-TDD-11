package com.tdd.bookshelf.core.ui.common.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Gray50
import com.tdd.bookshelf.core.designsystem.Gray600
import com.tdd.bookshelf.core.designsystem.Gray900

@Composable
fun TextFieldBox(
    textInput: String,
    onValueChange: (String) -> Unit,
    hintText: String,
) {
    TextFieldBoxContent(
        textInput = textInput,
        onValueChange = onValueChange,
        hintText = hintText,
    )
}

@Composable
fun TextFieldBoxContent(
    textInput: String = "",
    onValueChange: (String) -> Unit = {},
    hintText: String = "",
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            focusManager.clearFocus()
        }
    }

    Box(
        modifier =
            Modifier
                .height(60.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Gray50),
    ) {
        BasicTextField(
            value = textInput,
            onValueChange = { input ->
                onValueChange(input)
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
            textStyle =
                BookShelfTypo.Regular.copy(
                    color = Gray900,
                    fontSize = 16.sp,
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
                            style = BookShelfTypo.Regular,
                            color = Gray600,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}
