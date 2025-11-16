package com.tdd.bookshelf.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.img_chapter_detail
import coil3.compose.AsyncImage
import com.tdd.bookshelf.core.designsystem.BackGround1
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelf
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Fri
import com.tdd.bookshelf.core.designsystem.Gray1
import com.tdd.bookshelf.core.designsystem.HomeProgressFinish
import com.tdd.bookshelf.core.designsystem.HomeProgressTitle
import com.tdd.bookshelf.core.designsystem.HomeTitle
import com.tdd.bookshelf.core.designsystem.Main1
import com.tdd.bookshelf.core.designsystem.Mon
import com.tdd.bookshelf.core.designsystem.Sat
import com.tdd.bookshelf.core.designsystem.Sun
import com.tdd.bookshelf.core.designsystem.Thu
import com.tdd.bookshelf.core.designsystem.Tue
import com.tdd.bookshelf.core.designsystem.Wed
import com.tdd.bookshelf.core.designsystem.White3
import com.tdd.bookshelf.core.ui.common.content.BasicDivider
import com.tdd.bookshelf.core.ui.common.content.ItemContentBox
import com.tdd.bookshelf.core.ui.common.item.SelectCircleListItem
import com.tdd.bookshelf.core.ui.util.generateCalendarDays
import com.tdd.bookshelf.domain.entity.response.autobiography.CreatedMaterialIItemModel
import com.tdd.bookshelf.domain.entity.response.interview.MonthInterviewItemModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen(
    goToPastInterviewPage: (String) -> Unit,
) {
    val viewModel: HomeViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    val today = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
    var year by remember { mutableIntStateOf(today.year) }
    var month by remember { mutableIntStateOf(today.monthNumber) }
    var days by remember { mutableStateOf(generateCalendarDays(year, month)) }

    HomeContent(
        interactionSource = interactionSource,
        createdMaterialList = uiState.createdMaterialList,
        interviewProgress = uiState.autobiographyProgress,
        monthInterviewList = uiState.monthInterviewList,
        days = days,
        selectedDay = uiState.selectedDay,
        selectedDate = uiState.selectedDate,
        onSelectDay = { day -> viewModel.onClickInterviewDate(day) },
        onClickSummary = { goToPastInterviewPage(uiState.selectedDate) }
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    createdMaterialList: List<CreatedMaterialIItemModel> = emptyList(),
    interviewProgress: Int = 0,
    monthInterviewList: List<MonthInterviewItemModel> = emptyList(),
    days: List<LocalDate> = emptyList(),
    selectedDay: Int = 0,
    selectedDate: String = "",
    onSelectDay: (Int) -> Unit = {},
    onClickSummary: () -> Unit = {},
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
            interviewList = monthInterviewList,
            interactionSource = interactionSource,
            days = days,
            onSelectDay = onSelectDay
        )

        HomeInterviewSummary(
            selectedDate = selectedDate,
            selectedDateSummary = monthInterviewList[selectedDay].summary,
            interactionSource = interactionSource,
            onClick = onClickSummary
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
    interactionSource: MutableInteractionSource,
    days: List<LocalDate>,
    onSelectDay: (Int) -> Unit,
) {
    ItemContentBox(
        modifier = modifier,
        paddingTop = 20,
        paddingStart = 20,
        paddingEnd = 20,
        content = {
            Column {
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

                CalendarDayOfMonth(
                    interactionSource = interactionSource,
                    days = days,
                    onSelectDay = onSelectDay,
                    selectedDay = selectedDay,
                    interviewList = interviewList
                )
            }
        }
    )

//    Column(
//        modifier = modifier
//            .fillMaxWidth()
//            .padding(top = 20.dp, start = 20.dp, end = 20.dp)
//            .clip(RoundedCornerShape(5.dp))
//            .background(BackGround1)
//            .border(1.dp, Gray1, RoundedCornerShape(5.dp))
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(start = 20.dp, end = 10.dp, top = 15.dp, bottom = 20.dp)
//        ) {
//            Text(
//                text = selectedDate,
//                color = Black1,
//                style = BookShelfTypo.Body3,
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//            )
//
//            Text(
//                text = "${selectedDay}일 ${interviewList[selectedDay].totalAnswerCount}번의 대화 수행",
//                color = Main1,
//                style = BookShelfTypo.Caption4,
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//            )
//        }
//
//        CalendarWeekTitle()
//
//        CalendarDayOfMonth(
//            interactionSource = interactionSource,
//            days = days,
//            onSelectDay = onSelectDay,
//            selectedDay = selectedDay,
//            interviewList = interviewList
//        )
//    }
}

@Composable
private fun CalendarWeekTitle() {
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

@Composable
private fun CalendarDayOfMonth(
    days: List<LocalDate>,
    interactionSource: MutableInteractionSource,
    onSelectDay: (Int) -> Unit,
    selectedDay: Int,
    interviewList: List<MonthInterviewItemModel>,
) {
    Column(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 20.dp, start = 24.dp, end = 24.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                week.forEach { day ->
                    val index = day.dayOfMonth - 1

                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        CalendarDateItem(
                            day = day.dayOfMonth,
                            modifier = Modifier.width(maxWidth).fillMaxHeight(),
                            interactionSource = interactionSource,
                            onSelect = { onSelectDay(day.dayOfMonth) },
                            isSelectedDate = (day.dayOfMonth == selectedDay),
                            isInterviewNumNotZero = (interviewList[day.dayOfMonth].totalAnswerCount != 0)
                        )
                    }
                }

                if (week.size < 7) {
                    repeat(7 - week.size) {
                        Spacer(modifier = Modifier.weight(1f).fillMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDateItem(
    day: Int,
    modifier: Modifier,
    interactionSource: MutableInteractionSource,
    onSelect: () -> Unit,
    isSelectedDate: Boolean,
    isInterviewNumNotZero: Boolean,
) {
    Column(
        modifier = modifier
            .clickable(
                enabled = isInterviewNumNotZero,
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (isInterviewNumNotZero) Main1 else BackGround1)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                color = if (isInterviewNumNotZero) White3 else Black1,
                style = BookShelfTypo.Body4,
                textAlign = TextAlign.Center,
            )
        }

        if (isSelectedDate) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Main1)
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun HomeInterviewSummary(
    selectedDate: String,
    selectedDateSummary: String,
    onClick: () -> Unit,
    interactionSource: MutableInteractionSource,
) {
    ItemContentBox(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        paddingTop = 15,
        paddingBottom = 15,
        paddingStart = 20,
        paddingEnd = 20,
        content = {
            Column {
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
    )


//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 15.dp)
//            .clip(RoundedCornerShape(5.dp))
//            .background(BackGround1)
//            .border(1.dp, Gray1, RoundedCornerShape(5.dp))
//            .clickable(
//                interactionSource = interactionSource,
//                indication = null,
//                onClick = onClick
//            )
//    ) {
//        Text(
//            text = selectedDate,
//            color = Black1,
//            style = BookShelfTypo.Body4,
//            modifier = Modifier
//                .padding(top = 15.dp, start = 20.dp)
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 10.dp, vertical = 12.dp)
//        ) {
//            Text(
//                text = selectedDateSummary,
//                color = Black1,
//                style = BookShelfTypo.Caption4,
//                modifier = Modifier
//                    .weight(1f)
//            )
//
//            AsyncImage(
//                model = Res.getUri("files/ic_right.svg"),
//                contentDescription = "move",
//                modifier = Modifier
//                    .padding(end = 4.dp)
//                    .align(Alignment.Bottom)
//                    .size(24.dp),
//            )
//        }
//    }
}

@Preview
@Composable
fun PreviewHome() {
    HomeContent()
}
