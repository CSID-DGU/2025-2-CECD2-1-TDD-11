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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.bookshelf.core.designsystem.BackGround1
import com.tdd.bookshelf.core.designsystem.Blue500
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.EmailHintText
import com.tdd.bookshelf.core.designsystem.PasswordHintText
import com.tdd.bookshelf.core.designsystem.SignUpBtn
import com.tdd.bookshelf.core.designsystem.SignUpTitle
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.item.TextFieldBox
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SignUpScreen(
    goToLogInPage: () -> Unit,
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpEvent.GoToLogInPage -> {
                    goToLogInPage()
                }
            }
        }
    }

    SignUpContent(
        onClickSignUpBtn = { viewModel.postEmailSignUp() },
        emailInput = uiState.emailInput,
        onEmailValueChange = { newValue -> viewModel.onEmailValueChange(newValue) },
        passwordInput = uiState.passwordInput,
        onPasswordValueChange = { newValue -> viewModel.onPasswordValueChange(newValue) },
    )
}

@Composable
private fun SignUpContent(
    onClickSignUpBtn: () -> Unit = {},
    emailInput: String = "",
    onEmailValueChange: (String) -> Unit = {},
    passwordInput: String = "",
    onPasswordValueChange: (String) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround1),
    ) {
        Text(
            text = SignUpTitle,
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

        Spacer(modifier = Modifier.padding(top = 60.dp))

        RectangleBtn(
            btnContent = SignUpBtn,
            isBtnActivated = emailInput.isNotEmpty() && passwordInput.isNotEmpty(),
            onClickAction = onClickSignUpBtn,
        )
    }
}
