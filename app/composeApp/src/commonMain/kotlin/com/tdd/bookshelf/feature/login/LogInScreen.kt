package com.tdd.bookshelf.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.bookshelf.core.designsystem.BackGround1
import com.tdd.bookshelf.core.designsystem.Blue500
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.EmailHintText
import com.tdd.bookshelf.core.designsystem.LogInBtn
import com.tdd.bookshelf.core.designsystem.LogInTitle
import com.tdd.bookshelf.core.designsystem.PasswordHintText
import com.tdd.bookshelf.core.designsystem.SignUpTitle
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.item.TextFieldBox
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LogInScreen(
    goToOnboardingPage: () -> Unit,
    goToSignUp: () -> Unit,
) {
    val viewModel: LogInViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LogInEvent.GoToOnBoardingPage -> {
                    goToOnboardingPage()
                }
            }
        }
    }

    LogInContent(
        onClickLogInBtn = { viewModel.postEmailLogIn() },
        emailInput = uiState.emailInput,
        onEmailValueChange = { newValue -> viewModel.onEmailValueChange(newValue) },
        passwordInput = uiState.passwordInput,
        onPasswordValueChange = { newValue -> viewModel.onPasswordValueChange(newValue) },
        onClickSignUp = { goToSignUp() },
    )
}

@Composable
private fun LogInContent(
    onClickLogInBtn: () -> Unit = {},
    emailInput: String = "",
    onEmailValueChange: (String) -> Unit = {},
    passwordInput: String = "",
    onPasswordValueChange: (String) -> Unit = {},
    onClickSignUp: () -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround1),
    ) {
        Text(
            text = LogInTitle,
            style = BookShelfTypo.SemiBold,
            color = Blue500,
            fontSize = 32.sp,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 180.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.padding(top = 150.dp))

        TextFieldBox(
            textInput = emailInput,
            onValueChange = onEmailValueChange,
            hintText = EmailHintText,
        )

        Spacer(modifier = Modifier.padding(top = 16.dp))

        TextFieldBox(
            textInput = passwordInput,
            onValueChange = onPasswordValueChange,
            hintText = PasswordHintText,
        )

        Text(
            text = SignUpTitle,
            style = BookShelfTypo.SemiBold,
            color = Blue500,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .align(Alignment.Start)
                    .padding(top = 20.dp, start = 25.dp)
                    .clickable(
                        onClick = onClickSignUp,
                    ),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.padding(top = 60.dp))

        RectangleBtn(
            btnContent = LogInBtn,
            isBtnActivated = emailInput.isNotEmpty() && passwordInput.isNotEmpty(),
            onClickAction = onClickLogInBtn,
        )
    }
}

@Preview
@Composable
fun PreviewLogIn() {
    LogInContent()
}
