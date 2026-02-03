package com.tdd.talktobook.feature.auth.signup

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
import talktobook.composeapp.generated.resources.Res
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.EmailHintText
import com.tdd.talktobook.core.designsystem.PasswordHintText
import com.tdd.talktobook.core.designsystem.ServerErrorToast
import com.tdd.talktobook.core.designsystem.SignUpEmailError
import com.tdd.talktobook.core.designsystem.SignUpMemberExistAlready
import com.tdd.talktobook.core.designsystem.SignUpPassWordError
import com.tdd.talktobook.core.designsystem.SignUpText
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.textfield.TextFieldBox
import com.tdd.talktobook.core.ui.common.type.ToastType
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SignUpScreen(
    goToEmailCheckPage: (String) -> Unit,
    goToPasswordChangePage: () -> Unit,
    showToastMsg: (String, ToastType) -> Unit,
    onClickBackBtn: () -> Unit,
) {
    val viewModel: SignUpViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is SignUpEvent.GoToEmailCheckPage -> {
                    goToEmailCheckPage(uiState.emailInput)
                }

                is SignUpEvent.ShowMemberExistToast -> {
                    showToastMsg(SignUpMemberExistAlready, ToastType.INFO)
                }

                is SignUpEvent.ShowServerErrorToast -> {
                    showToastMsg(ServerErrorToast, ToastType.ERROR)
                }
            }
        }
    }

    SignUpContent(
        interactionSource = interactionSource,
        onClickSignUpBtn = { viewModel.checkEmailPWValid() },
        emailInput = uiState.emailInput,
        onEmailValueChange = { newValue -> viewModel.onEmailValueChange(newValue) },
        isEmailValid = uiState.isEmailValid,
        passwordInput = uiState.passwordInput,
        onPasswordValueChange = { newValue -> viewModel.onPasswordValueChange(newValue) },
        isPasswordValid = uiState.isPasswordValid,
        onClickChangePassword = { goToPasswordChangePage() },
        onClickBackBtn = onClickBackBtn,
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SignUpContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClickSignUpBtn: () -> Unit = {},
    emailInput: String = "",
    onEmailValueChange: (String) -> Unit = {},
    isEmailValid: Boolean = true,
    passwordInput: String = "",
    onPasswordValueChange: (String) -> Unit = {},
    isPasswordValid: Boolean = true,
    onClickChangePassword: () -> Unit,
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
            text = SignUpText,
            style = BookShelfTypo.Head20,
            color = Black1,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 100.dp),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.padding(top = 60.dp))

        TextFieldBox(
            textInput = emailInput,
            onValueChange = onEmailValueChange,
            hintText = EmailHintText,
            isError = !isEmailValid,
            errorText = SignUpEmailError,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        TextFieldBox(
            textInput = passwordInput,
            onValueChange = onPasswordValueChange,
            hintText = PasswordHintText,
            isError = !isPasswordValid,
            errorText = SignUpPassWordError,
        )

        Spacer(modifier = Modifier.padding(top = 15.dp))

        // TODO 비밀번호 초기화
//        UnderLineTextBtn(
//            textContent = ChangePasswordText,
//            interactionSource = interactionSource,
//            textColor = Gray5,
//            paddingEnd = 25,
//            onClick = onClickChangePassword,
//        )

        Spacer(modifier = Modifier.weight(1f))

        RectangleBtn(
            btnContent = SignUpText,
            isBtnActivated = emailInput.isNotEmpty() && passwordInput.isNotEmpty(),
            onClickAction = onClickSignUpBtn,
        )

        Spacer(modifier = Modifier.padding(bottom = 80.dp))
    }
}
