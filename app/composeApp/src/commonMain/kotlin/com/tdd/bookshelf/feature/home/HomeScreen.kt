package com.tdd.bookshelf.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.ic_send
import bookshelf.composeapp.generated.resources.img_chapter_default
import bookshelf.composeapp.generated.resources.img_chapter_detail
import bookshelf.composeapp.generated.resources.img_current_chapter_default
import coil3.compose.AsyncImage
import com.tdd.bookshelf.core.designsystem.BackGround1
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.BackGround3
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelf
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Fri
import com.tdd.bookshelf.core.designsystem.Gray1
import com.tdd.bookshelf.core.designsystem.HomeCurrentChapterEmpty
import com.tdd.bookshelf.core.designsystem.HomeCurrentProgressTitle
import com.tdd.bookshelf.core.designsystem.HomeProgressFinish
import com.tdd.bookshelf.core.designsystem.HomeProgressTitle
import com.tdd.bookshelf.core.designsystem.HomeTitle
import com.tdd.bookshelf.core.designsystem.Main1
import com.tdd.bookshelf.core.designsystem.Mon
import com.tdd.bookshelf.core.designsystem.Neutral500
import com.tdd.bookshelf.core.designsystem.Neutral900
import com.tdd.bookshelf.core.designsystem.Sat
import com.tdd.bookshelf.core.designsystem.Sun
import com.tdd.bookshelf.core.designsystem.Thu
import com.tdd.bookshelf.core.designsystem.Tue
import com.tdd.bookshelf.core.designsystem.Wed
import com.tdd.bookshelf.core.designsystem.White0
import com.tdd.bookshelf.core.designsystem.White100
import com.tdd.bookshelf.core.ui.common.content.BasicDivider
import com.tdd.bookshelf.core.ui.common.item.SelectCircleListItem
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.bookshelf.domain.entity.response.autobiography.CreatedMaterialIItemModel
import com.tdd.bookshelf.domain.entity.response.autobiography.SubChapterItemModel
import com.tdd.bookshelf.domain.entity.response.interview.MonthInterviewItemModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    goToInterviewPage: (Int) -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    HomeContent(
        interactionSource = interactionSource,
        createdMaterialList = uiState.createdMaterialList,
        interviewProgress = uiState.autobiographyProgress,
        monthInterviewList = uiState.monthInterviewList,
        selectedDay = uiState.selectedDay,
        selectedDate = uiState.selectedDate,
        onSelectDay = { day -> viewModel.onClickInterviewDate(day) }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    createdMaterialList: List<CreatedMaterialIItemModel> = emptyList(),
    interviewProgress: Int = 0,
    monthInterviewList: List<MonthInterviewItemModel> = emptyList(),
    selectedDay: Int = 0,
    selectedDate: String = "",
    onSelectDay: (Int) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        HomeTopBar()

        Text(
            text = HomeTitle,
            color = Black1,
            style = BookShelfTypo.Body1,
            modifier = Modifier
                .padding(start = 20.dp)
        )

        HomeMaterialList(
            createdMaterialList = createdMaterialList
        )

        BasicDivider()

        HomeProgress(
            progress = interviewProgress
        )

        HomeInterviewCalendar(
            modifier = Modifier.weight(1f),
            selectedDay = selectedDay,
            selectedDate = selectedDate,
            interviewList = monthInterviewList
        )

        HomeInterviewSummary(
            selectedDate = selectedDate,
            selectedDateSummary = monthInterviewList[selectedDay].summary
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 25.dp, bottom = 25.dp),
    ) {
        Text(
            text = BookShelf,
            color = Black1,
            style = BookShelfTypo.Head1,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )

        AsyncImage(
            model = Res.getUri("files/ic_home_settings.svg"),
            contentDescription = "setting",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(top = 20.dp, end = 15.dp)
                .size(24.dp),
        )
    }
}

@Composable
private fun HomeMaterialList(
    createdMaterialList: List<CreatedMaterialIItemModel>,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        itemsIndexed(createdMaterialList) { index, item ->
            SelectCircleListItem(
                itemImg = Res.drawable.img_chapter_detail,
                itemText = item.name,
                isSelected = true,
            )
        }
    }
}

@Composable
private fun HomeProgress(
    progress: Int,
) {
    val progressBox = (progress / 100f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 5.dp)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = HomeProgressTitle,
            color = Black1,
            style = BookShelfTypo.Body1,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )

        Text(
            text = "$HomeProgressFinish${progress}%",
            color = Main1,
            style = BookShelfTypo.Caption2,
            modifier = Modifier
                .align(Alignment.BottomEnd)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(5.dp)
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Gray1)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progressBox)
                .height(5.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Main1)
        )
    }
}

@Composable
private fun HomeInterviewCalendar(
    modifier: Modifier,
    selectedDate: String,
    selectedDay: Int,
    interviewList: List<MonthInterviewItemModel>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(BackGround1)
            .border(1.dp, Gray1, RoundedCornerShape(5.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp, top = 15.dp, bottom = 20.dp)
        ) {
            Text(
                text = selectedDate,
                color = Black1,
                style = BookShelfTypo.Body3,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            )

            Text(
                text = "${selectedDay}일 ${interviewList[selectedDay].totalAnswerCount}번의 대화 수행",
                color = Main1,
                style = BookShelfTypo.Caption4,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            )
        }

        CalendarWeekTitle()
    }
}

@Composable
fun CalendarWeekTitle() {
    val weeks: List<String> = listOf(Sun, Mon, Tue, Wed, Thu, Fri, Sat)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        weeks.forEach { week ->
            Text(
                text = week,
                color = Black1,
                style = BookShelfTypo.Body3,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeInterviewSummary(
    selectedDate: String,
    selectedDateSummary: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(BackGround1)
            .border(1.dp, Gray1, RoundedCornerShape(5.dp))
    ) {
        Text(
            text = selectedDate,
            color = Black1,
            style = BookShelfTypo.Body4,
            modifier = Modifier
                .padding(top = 15.dp, start = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Text(
                text = selectedDateSummary,
                color = Black1,
                style = BookShelfTypo.Caption4,
                modifier = Modifier
                    .weight(1f)
            )

            AsyncImage(
                model = Res.getUri("files/ic_right.svg"),
                contentDescription = "move",
                modifier = Modifier
                    .padding(end = 4.dp)
                    .align(Alignment.Bottom)
                    .size(24.dp),
            )
        }
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
