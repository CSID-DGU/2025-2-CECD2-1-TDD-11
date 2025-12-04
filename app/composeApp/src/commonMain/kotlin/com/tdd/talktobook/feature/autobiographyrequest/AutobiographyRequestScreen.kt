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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.talktobook.core.designsystem.AutobiographyPdfName
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.CreateAutobiographyDialogTitle
import com.tdd.talktobook.core.designsystem.DownLoadPdf
import com.tdd.talktobook.core.designsystem.GoToHome
import com.tdd.talktobook.core.designsystem.Gray5
import com.tdd.talktobook.core.designsystem.RequestSuccessInCoShowFlow
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.common.button.UnderLineTextBtn
import com.tdd.talktobook.core.ui.common.content.LoadingContent
import com.tdd.talktobook.core.ui.util.rememberPdfDownloader
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AutobiographyRequestScreen(
    goToLogIn: () -> Unit,
) {
    val viewModel: AutobiographyRequestViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    val pdfDownloader = rememberPdfDownloader()


    AutobiographyRequestContent(
        onClickConfirmBtn = {
            pdfDownloader.download(
                url = uiState.pdfUrl,
                suggestedFileName = AutobiographyPdfName
            )
            goToLogIn()
        },
        onClickGoLogIn = { goToLogIn() },
        isSuccessDownload = uiState.isSuccessDownload,
        interactionSource = interactionSource
    )

    if (!uiState.isSuccessDownload) {
        LoadingContent()
    }
}

@Composable
fun AutobiographyRequestContent(
    onClickConfirmBtn: () -> Unit,
    onClickGoLogIn: () -> Unit,
    interactionSource: MutableInteractionSource,
    isSuccessDownload: Boolean,
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
            btnContent = DownLoadPdf,
            isBtnActivated = isSuccessDownload,
            onClickAction = onClickConfirmBtn,
        )

        Spacer(modifier = Modifier.height(20.dp))

        UnderLineTextBtn(
            interactionSource = interactionSource,
            textContent = GoToHome,
            textColor = Gray5,
            onClick = onClickGoLogIn,
            paddingEnd = 20
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}
