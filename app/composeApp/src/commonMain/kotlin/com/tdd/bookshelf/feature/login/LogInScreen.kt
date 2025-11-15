package com.tdd.bookshelf.feature.login

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.EmailHintText
import com.tdd.bookshelf.core.designsystem.LogInText
import com.tdd.bookshelf.core.designsystem.Main1
import com.tdd.bookshelf.core.designsystem.PasswordHintText
import com.tdd.bookshelf.core.designsystem.SignUpText
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.button.UnderLineTextBtn
import com.tdd.bookshelf.core.ui.common.textfield.TextFieldBox
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LogInScreen(
    goToOnboardingPage: () -> Unit,
    goToSignUp: () -> Unit,
    goToHome: () -> Unit,
) {
    val viewModel: LogInViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is LogInEvent.GoToOnBoardingPage -> {
                    goToOnboardingPage()
                }

                is LogInEvent.GoToHomePage -> {
                    goToHome()
                }
            }
        }
    }

    LogInContent(
        interactionSource = interactionSource,
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
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
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
                .background(BackGround2),
    ) {
        Text(
            text = LogInText,
            style = BookShelfTypo.Head20,
            color = Black1,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 180.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.padding(top = 60.dp))

        TextFieldBox(
            textInput = emailInput,
            onValueChange = onEmailValueChange,
            hintText = EmailHintText,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        TextFieldBox(
            textInput = passwordInput,
            onValueChange = onPasswordValueChange,
            hintText = PasswordHintText,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        UnderLineTextBtn(
            textContent = SignUpText,
            interactionSource = interactionSource,
            textColor = Main1,
            paddingEnd = 25,
            onClick = onClickSignUp
        )

        Spacer(modifier = Modifier.weight(1f))

        RectangleBtn(
            btnContent = LogInText,
            isBtnActivated = emailInput.isNotEmpty() && passwordInput.isNotEmpty(),
            onClickAction = onClickLogInBtn,
        )

        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}

@Preview
@Composable
fun PreviewLogIn() {
    LogInContent()
}
