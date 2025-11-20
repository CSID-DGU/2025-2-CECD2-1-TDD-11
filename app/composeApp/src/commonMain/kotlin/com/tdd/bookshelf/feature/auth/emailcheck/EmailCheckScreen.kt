package com.tdd.bookshelf.feature.auth.emailcheck

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
import com.tdd.bookshelf.core.designsystem.CodeHintText
import com.tdd.bookshelf.core.designsystem.Confirm
import com.tdd.bookshelf.core.designsystem.EmailCheckText
import com.tdd.bookshelf.core.ui.common.button.RectangleBtn
import com.tdd.bookshelf.core.ui.common.textfield.DisEnabledTextFieldBox
import com.tdd.bookshelf.core.ui.common.textfield.TextFieldBox
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun EmailCheckScreen(
    goToLogInPage: () -> Unit,
    email: String,
) {
    val viewModel: EmailCheckViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onClickCheckBtn = { viewModel.postCheckEmail() },
        email = uiState.email,
        codeInput = uiState.codeInput,
        onCodeValueChange = { newValue -> viewModel.onCodeValueChange(newValue) },
    )
}

@Composable
fun EmailCheckContent(
    onClickCheckBtn: () -> Unit,
    email: String,
    codeInput: String,
    onCodeValueChange: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        Text(
            text = EmailCheckText,
            style = BookShelfTypo.Head20,
            color = Black1,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 180.dp),
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
