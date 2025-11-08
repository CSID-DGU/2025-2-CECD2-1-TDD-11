package com.tdd.interview.start.ready

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BackGround
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.CreateChapterSemiTitle
import com.tdd.design_system.CreateChapterTitle
import com.tdd.design_system.GoToHomeNotInterview
import com.tdd.design_system.Gray5
import com.tdd.design_system.InterViewStartBtn
import com.tdd.design_system.R
import com.tdd.ui.common.button.BottomRectangleBtn
import com.tdd.ui.common.content.TopTitleContent

@Composable
fun InterviewStartScreen(
    showInterviewDialog: () -> Unit,
    goHomePage: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    InterviewStartContent(
        interactionSource = interactionSource,
        showInterviewDialog = showInterviewDialog,
        onClickGoHomeBtn = { goHomePage() }
    )
}

@Composable
fun InterviewStartContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    showInterviewDialog: () -> Unit = {},
    onClickGoHomeBtn: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopTitleContent(
                title = CreateChapterTitle,
                semiTitle = CreateChapterSemiTitle
            )

            Image(
                painter = painterResource(id = R.drawable.ic_interview_mirror_example),
                contentDescription = "mirror interview",
                modifier = Modifier
                    .padding(60.dp)
                    .size(310.dp)
            )
        }

        BottomRectangleBtn(
            btnTextContent = InterViewStartBtn,
            isBtnActivated = true,
            onClickAction = showInterviewDialog
        )

        Text(
            text = GoToHomeNotInterview,
            color = Gray5,
            style = BookShelfTypo.head30.copy(
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 10.dp, bottom = 35.dp)
                .clickable(
                    onClick = onClickGoHomeBtn,
                    interactionSource = interactionSource,
                    indication = null
                )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewStartInterview() {
    InterviewStartContent()
}