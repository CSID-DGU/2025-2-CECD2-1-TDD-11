package com.tdd.bookshelf.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.ic_send
import bookshelf.composeapp.generated.resources.img_chapter_default
import bookshelf.composeapp.generated.resources.img_current_chapter_default
import coil3.compose.AsyncImage
import com.tdd.bookshelf.core.designsystem.BackGround3
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.HomeCurrentChapterEmpty
import com.tdd.bookshelf.core.designsystem.HomeCurrentProgressTitle
import com.tdd.bookshelf.core.designsystem.HomeSemiTitle
import com.tdd.bookshelf.core.designsystem.HomeTitle
import com.tdd.bookshelf.core.designsystem.Neutral500
import com.tdd.bookshelf.core.designsystem.Neutral900
import com.tdd.bookshelf.core.designsystem.White0
import com.tdd.bookshelf.core.designsystem.White100
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.bookshelf.domain.entity.response.autobiography.SubChapterItemModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    goToInterviewPage: (Int) -> Unit,
    goToDetailChapterPage: (Int) -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is HomeEvent.GoToDetailChapterPage -> {
                    goToDetailChapterPage(viewModel.checkAutobiographyId())
                }
            }
        }
    }

    HomeContent(
        chapterList = uiState.chapterList,
        currentChapterId = uiState.currentChapterId,
        onClickCurrentChapterInterview = { goToInterviewPage(viewModel.setInterviewId()) },
        interactionSource = interactionSource,
        onClickChapterDetail = { detailChapterId ->
//            goToDetailChapterPage(viewModel.setAutobiographyId(detailChapterId))
            viewModel.setAutobiographyId(detailChapterId)
        },
        currentChapter = uiState.currentChapter,
    )
}

@Composable
private fun HomeContent(
    chapterList: List<ChapterItemModel> = emptyList(),
    currentChapterId: Int = 0,
    onClickCurrentChapterInterview: () -> Unit = {},
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClickChapterDetail: (Int) -> Unit = {},
    currentChapter: SubChapterItemModel = SubChapterItemModel(),
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(White0),
    ) {
        Text(
            text = HomeSemiTitle,
            color = Neutral500,
            style = BookShelfTypo.SemiBold,
            fontSize = 13.sp,
            modifier =
                Modifier
                    .padding(top = 45.dp, start = 25.dp),
        )

        Text(
            text = HomeTitle,
            color = Neutral900,
            style = BookShelfTypo.SemiBold,
            fontSize = 21.sp,
            modifier =
                Modifier
                    .padding(top = 2.dp, start = 25.dp),
        )

        Spacer(modifier = Modifier.padding(top = 28.dp))

        HomeChapter(
            currentChapterId = currentChapterId,
            chapterList = chapterList,
            modifier = Modifier.weight(1f),
            onClickCurrentChapterInterview = onClickCurrentChapterInterview,
            interactionSource = interactionSource,
            onClickChapterDetail = onClickChapterDetail,
            currentChapter = currentChapter,
        )
    }
}

@Composable
private fun HomeChapter(
    currentChapterId: Int,
    chapterList: List<ChapterItemModel>,
    modifier: Modifier,
    onClickCurrentChapterInterview: () -> Unit,
    interactionSource: MutableInteractionSource,
    onClickChapterDetail: (Int) -> Unit,
    currentChapter: SubChapterItemModel,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(BackGround3),
    ) {
        Spacer(modifier = Modifier.padding(top = 25.dp))

        HomeCurrentProgressBox(
            onClickAction = onClickCurrentChapterInterview,
            interactionSource = interactionSource,
            currentChapter = currentChapter,
        )

        Spacer(modifier = Modifier.padding(top = 50.dp))

        HomeChapterList(
            chapterList = chapterList,
            onClickChapterDetail = onClickChapterDetail,
        )
    }
}

@Composable
private fun HomeCurrentProgressBox(
    onClickAction: () -> Unit,
    interactionSource: MutableInteractionSource,
    currentChapter: SubChapterItemModel,
) {
    Column(
        modifier =
            Modifier
                .clickable(
                    onClick = onClickAction,
                    indication = null,
                    interactionSource = interactionSource,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth()
                    .height(90.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.img_current_chapter_default),
                contentDescription = "current chapter img",
                contentScale = ContentScale.FillBounds,
                modifier =
                    Modifier
                        .fillMaxSize(),
            )

            Box(
                modifier =
                    Modifier
                        .padding(end = 53.dp)
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(topEnd = 8.dp))
                        .background(White100),
            ) {
                Text(
                    text = HomeCurrentProgressTitle,
                    color = White0,
                    style = BookShelfTypo.Regular,
                    fontSize = 13.sp,
                    modifier =
                        Modifier
                            .padding(top = 8.dp, bottom = 8.dp, start = 17.dp),
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .padding(horizontal = 28.dp)
                    .fillMaxWidth(),
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(end = 14.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(White0),
            ) {
                Text(
                    text = currentChapter.chapterName.ifEmpty { HomeCurrentChapterEmpty },
                    color = Neutral900,
                    style = BookShelfTypo.SemiBold,
                    fontSize = 17.sp,
                    modifier =
                        Modifier
                            .padding(top = 10.dp, start = 17.dp, bottom = 10.dp),
                )
            }

            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(7.dp))
                        .background(White0),
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_send),
                    contentDescription = "chapter start",
                    modifier =
                        Modifier
                            .padding(7.dp)
                            .size(21.dp),
                )
            }
        }
    }
}

@Composable
private fun HomeChapterList(
    chapterList: List<ChapterItemModel>,
    onClickChapterDetail: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(start = 22.dp, end = 22.dp, bottom = 30.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(17.dp),
    ) {
        chapterList.forEachIndexed { index, chapterItem ->
            Text(
                text = chapterItem.chapterName,
                style = BookShelfTypo.SemiBold,
                fontSize = 17.sp,
                color = Neutral900,
                modifier =
                    Modifier
                        .padding(bottom = 5.dp),
            )

            Column(
                modifier =
                    Modifier
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(17.dp),
            ) {
                chapterItem.subChapters.forEachIndexed { subIndex, subChapterItem ->
                    HomeSubChapterListItem(
                        subItem = subChapterItem,
                        onClickAction = { onClickChapterDetail(subChapterItem.chapterId) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeSubChapterListItem(
    subItem: SubChapterItemModel,
    onClickAction: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClickAction,
                ),
    ) {
        AsyncImage(
            model = Res.getUri("files/ic_chapter_circle.svg"),
            contentDescription = "",
            modifier =
                Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 12.dp)
                    .size(14.dp),
        )

        Row(
            modifier =
                Modifier
                    .weight(1f),
        ) {
            Image(
                painter = painterResource(Res.drawable.img_chapter_default),
                contentDescription = "sub chapter img",
                modifier =
                    Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)),
            )

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(86.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(White0),
            ) {
                Text(
                    text = subItem.chapterNumber,
                    color = Neutral500,
                    style = BookShelfTypo.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 15.dp, start = 11.dp),
                )
                Text(
                    text = subItem.chapterName,
                    color = Neutral900,
                    style = BookShelfTypo.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(start = 12.dp, top = 5.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHome() {
    HomeContent()
}
