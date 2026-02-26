package com.tdd.talktobook.core.ui.common.bottomsheet

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import com.tdd.talktobook.core.ui.util.daysInMonth
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun SelectedDateBottomSheet(
    monthStateVisibleIndex: Int,
    dayStateVisibleIndex: Int,
    yearStateVisibleIndex: Int,
    monthList: List<String>,
    dayList: List<String>,
    yearList: List<String>,
    titleText: String,
    btnText: String,
    onSelectItem: (String, String, String) -> Unit
) {
    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = monthStateVisibleIndex)
    val dayState = rememberLazyListState(initialFirstVisibleItemIndex = dayStateVisibleIndex)
    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = yearStateVisibleIndex)

    val initialMonth = remember { selectedValue(monthState, monthList) }
    val initialDay = remember { selectedValue(dayState, dayList) }
    val initialYear = remember { selectedValue(yearState, yearList) }

    val currentMonth by remember { derivedStateOf { selectedValue(monthState, monthList) } }
    val currentDay by remember { derivedStateOf { selectedValue(dayState, dayList) } }
    val currentYear by remember { derivedStateOf { selectedValue(yearState, yearList) } }

    val currentDaysInMonth by remember {
        derivedStateOf { daysInMonth(currentYear.toInt(), currentMonth.toInt()) }
    }
    val currentDayList by remember(currentYear, currentMonth) {
        mutableStateOf((1..currentDaysInMonth).map { it.toString() })
    }

    val isBtnActivated by remember {
        derivedStateOf {
            currentMonth != initialMonth ||
                    currentDay != initialDay ||
                    currentYear != initialYear
        }
    }

    LaunchedEffect(monthList, dayList, yearList) {
        snapshotFlow { monthState.layoutInfo.totalItemsCount }
            .filter { it > 0 }
            .first()

        monthState.scrollToItem(monthStateVisibleIndex.coerceAtLeast(0))
        dayState.scrollToItem(dayStateVisibleIndex.coerceAtLeast(0))
        yearState.scrollToItem(yearStateVisibleIndex.coerceAtLeast(0))
    }

    LaunchedEffect(currentYear, currentMonth) {
        val selectedDay = selectedValue(dayState, currentDayList).toIntOrNull() ?: 1
        val clampedDay = selectedDay.coerceIn(1, currentDaysInMonth)

        val targetIndex = (clampedDay - 1).coerceAtLeast(0)
        dayState.scrollToItem(targetIndex)
    }

    LazyBottomSheetContent(
        firstState = monthState,
        secondState = dayState,
        thirdState = yearState,
        firstList = monthList,
        secondList = currentDayList,
        thirdList = yearList,
        titleText = titleText,
        btnText = btnText,
        onClickBtnAction = { onSelectItem(selectedValue(monthState, monthList), selectedValue(dayState, currentDayList), selectedValue(yearState, yearList)) },
        isBtnActivated = isBtnActivated
    )
}