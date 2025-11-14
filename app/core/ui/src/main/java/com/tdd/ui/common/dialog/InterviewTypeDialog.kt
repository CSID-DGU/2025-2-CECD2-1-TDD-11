package com.tdd.ui.common.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tdd.design_system.AppInterviewBtnContent
import com.tdd.design_system.AppInterviewType
import com.tdd.design_system.BackGround
import com.tdd.design_system.Black1
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray5
import com.tdd.design_system.InterviewDialogSemiTitle
import com.tdd.design_system.InterviewDialogTitle
import com.tdd.design_system.MirrorInterviewBtnContent
import com.tdd.design_system.MirrorInterviewType
import com.tdd.design_system.R
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent
import com.tdd.ui.common.type.InterviewType

@Composable
fun InterviewTypeDialog(
    onSelectType: (InterviewType) -> Unit,
    onDismiss: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    InterviewTypeDialogContent(
        interactionSource = interactionSource,
        onSelectMirrorType = { onSelectType(InterviewType.MIRROR) },
        onSelectAppType = { onSelectType(InterviewType.APP) },
        onDismiss = onDismiss,
    )
}

@Composable
fun InterviewTypeDialogContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onSelectMirrorType: () -> Unit = {},
    onSelectAppType: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val horizontalPadding = 100.dp
            val maxDialogWidth = maxWidth - horizontalPadding * 2

            Card(
                modifier = Modifier
//                    .widthIn(max = maxDialogWidth)
//                    .padding(horizontal = horizontalPadding)
                    .wrapContentHeight()
                    .width(750.dp)
                ,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = BackGround
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopTitleContent(
                        title = InterviewDialogTitle,
                        semiTitle = InterviewDialogSemiTitle
                    )

                    Row(
                        modifier = Modifier
                            .padding(vertical = 50.dp)
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                        horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally)
                    ) {
                        InterviewTypeContent(
                            typeText = MirrorInterviewType,
                            typeImg = R.drawable.ic_interview_mirror,
                        )

                        InterviewTypeContent(
                            typeText = AppInterviewType,
                            typeImg = R.drawable.ic_interview_app,
                        )
                    }

                    BottomRectangleBtn(
                        btnTextContent = MirrorInterviewBtnContent,
                        isBtnActivated = true,
                        onClickAction = onSelectMirrorType
                    )

                    Text(
                        text = AppInterviewBtnContent,
                        color = Gray5,
                        style = BookShelfTypo.head30.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp, bottom = 35.dp)
                            .clickable(
                                onClick = onSelectAppType,
                                interactionSource = interactionSource,
                                indication = null
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun InterviewTypeContent(
    typeText: String,
    typeImg: Int,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = typeImg),
            contentDescription = "interview type",
            modifier = Modifier
                .size(150.dp)
        )

        Text(
            text = typeText,
            color = Black1,
            style = BookShelfTypo.caption20,
            modifier = Modifier
                .padding(top = 6.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInterview() {
    InterviewTypeDialogContent()
}