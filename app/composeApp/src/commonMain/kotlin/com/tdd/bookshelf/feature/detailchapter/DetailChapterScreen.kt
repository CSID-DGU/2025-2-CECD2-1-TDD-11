package com.tdd.bookshelf.feature.detailchapter

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.img_chapter_detail
import bookshelf.composeapp.generated.resources.img_edit
import com.tdd.bookshelf.core.designsystem.BackGround3
import com.tdd.bookshelf.core.designsystem.Black900
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.ChatAgainBtn
import com.tdd.bookshelf.core.designsystem.DetailChapterTitle
import com.tdd.bookshelf.core.designsystem.Gray600
import com.tdd.bookshelf.core.designsystem.Gray800
import com.tdd.bookshelf.core.designsystem.Neutral900
import com.tdd.bookshelf.core.ui.common.content.TopBarContent
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun DetailChapterScreen(
    autobiographyId: Int,
    goBackPage: () -> Unit,
) {
    val viewModel: DetailChapterViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.setAutobiographyId(autobiographyId)
    }

    DetailChapterContent(
        chapterDetail = uiState.detailChapter,
        onClickBackBtn = { goBackPage() },
        interactionSource = interactionSource,
    )
}

@Composable
private fun DetailChapterContent(
    chapterDetail: AutobiographiesDetailModel = AutobiographiesDetailModel(),
    onClickBackBtn: () -> Unit = {},
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround3),
    ) {
        TopBarContent(
            content = DetailChapterTitle,
            interactionSource = interactionSource,
            onClickIcon = onClickBackBtn,
        )

        DetailTopBar(
            title = chapterDetail.title,
        )

        Image(
            painter = painterResource(Res.drawable.img_chapter_detail),
            contentDescription = "chapter edit",
            modifier =
                Modifier
                    .padding(horizontal = 27.dp)
                    .padding(top = 26.dp)
                    .fillMaxWidth()
                    .height(290.dp)
                    .clip(RoundedCornerShape(13.dp)),
        )

        DetailTitleBar(
            title = chapterDetail.title,
        )

        Text(
            text = chapterDetail.content,
            color = Gray800,
            style = BookShelfTypo.Medium,
            fontSize = 15.sp,
            modifier =
                Modifier
                    .padding(horizontal = 37.dp)
                    .padding(top = 12.dp, bottom = 30.dp)
                    .fillMaxWidth()
                    .weight(1f),
        )
    }
}

@Composable
private fun DetailTopBar(
    title: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 27.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Black900,
            style = BookShelfTypo.SemiBold,
            fontSize = 16.sp,
            modifier =
                Modifier
                    .weight(1f),
        )

        DetailChatAgainBtn(
            onClick = {},
        )
    }
}

@Composable
private fun DetailTitleBar(
    title: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 27.dp)
                .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Black900,
            style = BookShelfTypo.SemiBold,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .weight(1f),
        )

        Image(
            painter = painterResource(Res.drawable.img_edit),
            contentDescription = "chapter edit",
            modifier =
                Modifier
                    .padding(end = 5.dp)
                    .size(width = 35.dp, height = 45.dp),
        )
    }
}

@Composable
private fun DetailChatAgainBtn(
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Gray600, RoundedCornerShape(8.dp))
                .clickable(
                    onClick = onClick,
                ),
    ) {
        Text(
            text = ChatAgainBtn,
            color = Neutral900,
            style = BookShelfTypo.SemiBold,
            fontSize = 13.sp,
            modifier =
                Modifier
                    .padding(vertical = 7.dp, horizontal = 18.dp),
        )
    }
}

@Preview
@Composable
fun PreviewDetailChapter() {
    DetailChapterContent()
}
