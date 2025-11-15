package com.tdd.bookshelf.feature.signup

import androidx.compose.foundation.background
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.EmailHintText
import com.tdd.bookshelf.core.designsystem.PasswordHintText
import com.tdd.bookshelf.core.designsystem.SignUpText
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.button.UnderLineTextBtn
import com.tdd.bookshelf.core.ui.common.textfield.TextFieldBox
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SignUpScreen(
    goToEmailCheckPage: () -> Unit,
    goToPasswordChangePage: () -> Unit,
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpEvent.GoToEmailCheckPage -> {
                    goToEmailCheckPage()
                }
            }
        }
    }

    SignUpContent(
        interactionSource = interactionSource,
        onClickSignUpBtn = { viewModel.postEmailSignUp() },
        emailInput = uiState.emailInput,
        onEmailValueChange = { newValue -> viewModel.onEmailValueChange(newValue) },
        passwordInput = uiState.passwordInput,
        onPasswordValueChange = { newValue -> viewModel.onPasswordValueChange(newValue) },
        onClickChangePassword = { goToPasswordChangePage() }
    )
}

@Composable
private fun SignUpContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClickSignUpBtn: () -> Unit = {},
    emailInput: String = "",
    onEmailValueChange: (String) -> Unit = {},
    passwordInput: String = "",
    onPasswordValueChange: (String) -> Unit = {},
    onClickChangePassword: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        Text(
            text = SignUpText,
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
            textContent = ChangePasswordText,
            interactionSource = interactionSource,
            textColor = Gray5,
            paddingEnd = 25,
            onClick = onClickChangePassword
        )

        Spacer(modifier = Modifier.weight(1f))

        RectangleBtn(
            btnContent = SignUpText,
            isBtnActivated = emailInput.isNotEmpty() && passwordInput.isNotEmpty(),
            onClickAction = onClickSignUpBtn,
        )

        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}
