package com.tdd.talktobook.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import com.tdd.talktobook.core.designsystem.BackGround4
import com.tdd.talktobook.core.designsystem.Blue900
import com.tdd.talktobook.core.designsystem.SettingTitle
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingScreen(
    goBackPage: () -> Unit,
) {
    val viewModel: SettingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    SettingContent(
        interactionSource = interactionSource,
        onClickBack = { goBackPage() }
    )
}

@Composable
private fun SettingContent(
    interactionSource: MutableInteractionSource,
    onClickBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround4),
    ) {
        TopBarContent(
            content = SettingTitle,
            interactionSource = interactionSource,
            iconVisible = true,
            onClickIcon = onClickBack
        )

        SettingProfileBox(
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SettingProfileBox(
) {
    Row(
        modifier =
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Blue900),
    ) {
        AsyncImage(
            model = Res.getUri("files/ic_profile_default.svg"),
            contentDescription = "",
            modifier =
                Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 22.dp, top = 17.dp, bottom = 17.dp)
                    .size(42.dp),
        )
    }
}