package com.tdd.talktobook.core.ui.common.bottomsheet

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

@Composable
fun SelectedBottomSheet(
    firstStateVisibleIndex: Int,
    secondStateVisibleIndex: Int,
    thirdStateVisibleIndex: Int,
    firstList: List<String>,
    secondList: List<String>,
    thirdList: List<String>,
    titleText: String,
    btnText: String,
    onSelectItem: (String, String, String) -> Unit,
) {
    val firstState = rememberLazyListState(initialFirstVisibleItemIndex = firstStateVisibleIndex)
    val secondState = rememberLazyListState(initialFirstVisibleItemIndex = secondStateVisibleIndex)
    val thirdState = rememberLazyListState(initialFirstVisibleItemIndex = thirdStateVisibleIndex)

    val initialFirst = remember { selectedValue(firstState, firstList) }
    val initialSecond = remember { selectedValue(secondState, secondList) }
    val initialThird = remember { selectedValue(thirdState, thirdList) }

    val currentFirst by remember { derivedStateOf { selectedValue(firstState, firstList) } }
    val currentSecond by remember { derivedStateOf { selectedValue(secondState, secondList) } }
    val currentThird by remember { derivedStateOf { selectedValue(thirdState, thirdList) } }

    val isBtnActivated by remember {
        derivedStateOf {
            currentFirst != initialFirst ||
                currentSecond != initialSecond ||
                currentThird != initialThird
        }
    }

    LaunchedEffect(firstList, secondList, thirdList) {
        snapshotFlow { firstState.layoutInfo.totalItemsCount }
            .filter { it > 0 }
            .first()

        firstState.scrollToItem(firstStateVisibleIndex.coerceAtLeast(0))
        secondState.scrollToItem(secondStateVisibleIndex.coerceAtLeast(0))
        thirdState.scrollToItem(thirdStateVisibleIndex.coerceAtLeast(0))
    }

    LazyBottomSheetContent(
        firstState = firstState,
        secondState = secondState,
        thirdState = thirdState,
        firstList = firstList,
        secondList = secondList,
        thirdList = thirdList,
        titleText = titleText,
        btnText = btnText,
        onClickBtnAction = { onSelectItem(selectedValue(firstState, firstList), selectedValue(secondState, secondList), selectedValue(thirdState, thirdList)) },
        isBtnActivated = isBtnActivated,
    )
}
