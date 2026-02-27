package com.tdd.talktobook.core.ui.common.bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.textfield.ExplainTextFieldBox

@Composable
fun TextFieldBottomSheet(
    titleText: String,
    btnText: String,
    onClickConfirmBtnAction: (String) -> Unit,
    textFieldHintText: String,
) {
    var textInput by remember { mutableStateOf("") }

    TextFieldBottomSheetContent(
        titleText = titleText,
        btnText = btnText,
        textFieldHintText = textFieldHintText,
        onValueChange = { newValue ->
            textInput = newValue
        },
        textInput = textInput,
        isBtnActivated = textInput.isNotEmpty(),
        onClickBtnAction = { onClickConfirmBtnAction(textInput) }
    )
}

@Composable
private fun TextFieldBottomSheetContent(
    titleText: String,
    btnText: String,
    onClickBtnAction: () -> Unit,
    isBtnActivated: Boolean = false,
    textInput: String,
    onValueChange: (String) -> Unit,
    textFieldHintText: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = titleText,
            style = BookShelfTypo.Head1,
            color = Black1,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 20.dp)
        )

        ExplainTextFieldBox(
            textInput = textInput,
            onValueChange = onValueChange,
            hintText = textFieldHintText,
        )

        Spacer(modifier = Modifier.height(20.dp))

        RectangleBtn(
            btnContent = btnText,
            isBtnActivated = isBtnActivated,
            onClickAction = onClickBtnAction,
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}