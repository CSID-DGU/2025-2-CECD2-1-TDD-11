package com.tdd.talktobook.feature.autobiographyrequest

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Confirm
import com.tdd.talktobook.core.designsystem.CreateAutobiographyDialogTitle
import com.tdd.talktobook.core.designsystem.RequestSuccessInCoShowFlow
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun AutobiographyRequestScreen(
    goToLogIn: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    AutobiographyRequestContent(
        onClickConfirmBtn = { goToLogIn() },
    )
}

@Composable
fun AutobiographyRequestContent(
    onClickConfirmBtn: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        Text(
            text = CreateAutobiographyDialogTitle,
            color = Black1,
            style = BookShelfTypo.Head20,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 180.dp),
        )

        Text(
            text = RequestSuccessInCoShowFlow,
            color = Black1,
            style = BookShelfTypo.Head1,
            modifier =
                Modifier
                    .padding(top = 30.dp)
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
        )

        RectangleBtn(
            btnContent = Confirm,
            isBtnActivated = true,
            onClickAction = onClickConfirmBtn,
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Preview()
@Composable
private fun PreviewAutobiographyRequest() {
    AutobiographyRequestContent(
        onClickConfirmBtn = {},
    )
}
