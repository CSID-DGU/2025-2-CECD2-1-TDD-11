package com.tdd.talktobook.feature.auth.emailcheck

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.CodeHintText
import com.tdd.talktobook.core.designsystem.Confirm
import com.tdd.talktobook.core.designsystem.EmailCheckText
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.textfield.DisEnabledTextFieldBox
import com.tdd.talktobook.core.ui.common.textfield.TextFieldBox
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel
import talktobook.composeapp.generated.resources.Res

@Composable
internal fun EmailCheckScreen(
    goToLogInPage: () -> Unit,
    email: String,
    onClickBackBtn: () -> Unit,
) {
    val viewModel: EmailCheckViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.setEmail(email)
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is EmailCheckEvent.GoToLogInPage -> {
                    goToLogInPage()
                }
            }
        }
    }

    EmailCheckContent(
        interactionSource = interactionSource,
        onClickCheckBtn = { viewModel.postCheckEmail() },
        email = uiState.email,
        codeInput = uiState.codeInput,
        onCodeValueChange = { newValue -> viewModel.onCodeValueChange(newValue) },
        onClickBackBtn = onClickBackBtn,
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun EmailCheckContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClickCheckBtn: () -> Unit,
    email: String,
    codeInput: String,
    onCodeValueChange: (String) -> Unit,
    onClickBackBtn: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        AsyncImage(
            model = Res.getUri("files/ic_back.svg"),
            contentDescription = "back",
            modifier =
                Modifier
                    .align(Alignment.Start)
                    .padding(start = 20.dp, top = 30.dp)
                    .size(24.dp)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClickBackBtn,
                    ),
        )

        Text(
            text = EmailCheckText,
            style = BookShelfTypo.Head20,
            color = Black1,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 100.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.padding(top = 60.dp))

        DisEnabledTextFieldBox(
            textContent = email,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        TextFieldBox(
            textInput = codeInput,
            onValueChange = onCodeValueChange,
            hintText = CodeHintText,
        )

        Spacer(modifier = Modifier.weight(1f))

        RectangleBtn(
            btnContent = Confirm,
            isBtnActivated = codeInput.isNotEmpty(),
            onClickAction = onClickCheckBtn,
        )

        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}
