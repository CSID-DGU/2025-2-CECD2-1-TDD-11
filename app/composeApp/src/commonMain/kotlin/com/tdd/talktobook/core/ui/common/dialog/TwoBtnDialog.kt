package com.tdd.talktobook.core.ui.common.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray5
import com.tdd.talktobook.core.designsystem.White3
import com.tdd.talktobook.core.ui.common.button.RectangleBtn


@Composable
fun TwoBtnDialog(
    title: String,
    semiTitle: String,
    firstBtnText: String,
    onClickFirstBtn: () -> Unit,
    secondBtnText: String,
    onClickSecondBtn: () -> Unit,
    isBottomTextVisible: Boolean,
    bottomText: String,
    onClickBottomText: () -> Unit,
    onDismiss: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    TwoBtnDialogContent(
        interactionSource = interactionSource,
        title = title,
        semiTitle = semiTitle,
        isBottomTextVisible = isBottomTextVisible,
        bottomText = bottomText,
        onClickBottomText = onClickBottomText,
        onDismiss = onDismiss,
        firstBtnText = firstBtnText,
        onClickFirstBtn = onClickFirstBtn,
        secondBtnText = secondBtnText,
        onClickSecondBtn = onClickSecondBtn,
    )
}

@Composable
fun TwoBtnDialogContent(
    interactionSource: MutableInteractionSource,
    title: String,
    semiTitle: String,
    firstBtnText: String,
    onClickFirstBtn: () -> Unit,
    secondBtnText: String,
    onClickSecondBtn: () -> Unit,
    isBottomTextVisible: Boolean,
    bottomText: String,
    onClickBottomText: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier =
                Modifier
                    .wrapContentHeight()
                    .width(266.dp),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(contentColor = White3),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    color = Black1,
                    style = BookShelfTypo.Head3,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .padding(top = 25.dp),
                )

                Text(
                    text = semiTitle,
                    color = Gray5,
                    style = BookShelfTypo.Body4,
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier
                            .padding(top = 20.dp, bottom = 30.dp),
                )

                RectangleBtn(
                    btnContent = firstBtnText,
                    isBtnActivated = true,
                    onClickAction = onClickFirstBtn,
                )

                Spacer(modifier = Modifier.padding(10.dp))

                RectangleBtn(
                    btnContent = secondBtnText,
                    isBtnActivated = true,
                    onClickAction = onClickSecondBtn,
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Spacer(modifier = Modifier.padding(if (isBottomTextVisible) 10.dp else 20.dp))

                if (isBottomTextVisible) {
                    Text(
                        text = bottomText,
                        color = Gray5,
                        style =
                            BookShelfTypo.Body2.copy(
                                textDecoration = TextDecoration.Underline,
                            ),
                        textAlign = TextAlign.Center,
                        modifier =
                            Modifier
                                .padding(bottom = 15.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = onClickBottomText,
                                ),
                    )
                }
            }
        }
    }
}
