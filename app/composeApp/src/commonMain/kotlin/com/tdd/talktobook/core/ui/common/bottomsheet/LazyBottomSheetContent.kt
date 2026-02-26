package com.tdd.talktobook.core.ui.common.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray1
import com.tdd.talktobook.core.designsystem.ZeroString
import com.tdd.talktobook.core.ui.common.button.RectangleBtn
import com.tdd.talktobook.core.ui.util.fadingEdge

@Composable
fun LazyBottomSheetContent(
    firstState: LazyListState,
    secondState: LazyListState,
    thirdState: LazyListState,
    firstList: List<String>,
    secondList: List<String>,
    thirdList: List<String>,
    titleText: String,
    btnText: String,
    onClickBtnAction: () -> Unit,
    isBtnActivated: Boolean = false
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = titleText,
            style = BookShelfTypo.Head1,
            color = Black1,
            modifier = Modifier
                .padding(top = 20.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 20.dp, horizontal = 60.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Gray1)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ListItem(
                    modifier = Modifier.weight(1f),
                    list = firstList,
                    state = firstState
                )

                ListItem(
                    modifier = Modifier.weight(1f),
                    list = secondList,
                    state = secondState
                )

                ListItem(
                    modifier = Modifier.weight(1f),
                    list = thirdList,
                    state = thirdState
                )

            }
        }

        RectangleBtn(
            btnContent = btnText,
            isBtnActivated = isBtnActivated,
            onClickAction = onClickBtnAction,
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ListItem(
    modifier: Modifier,
    state: LazyListState,
    list: List<String>,
) {
    val extendedItems = listOf(ZeroString, ZeroString) + list + listOf(ZeroString, ZeroString)
    val visibleItemsCount = 5
    val itemHeight = 35.dp
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    val fadingEdgeGradient =
        remember {
            Brush.verticalGradient(
                0f to Color.Transparent,
                0.5f to Black1,
                1f to Color.Transparent,
            )
        }

    LazyColumn(
        state = state,
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .fadingEdge(fadingEdgeGradient),
        flingBehavior = flingBehavior
    ) {
        items(extendedItems.size) { index ->
            val item = extendedItems[index]
            val firstVisibleItemIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
            val fontStyle =
                when (index) {
                    firstVisibleItemIndex + 2 -> BookShelfTypo.Head1
                    firstVisibleItemIndex + 1, firstVisibleItemIndex + 3 -> BookShelfTypo.Head3
                    firstVisibleItemIndex, firstVisibleItemIndex + 4 -> BookShelfTypo.Body1
                    else -> BookShelfTypo.Body2
                }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center
            ) {
                if (item != ZeroString) {
                    Text(
                        text = item,
                        style = fontStyle,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp)
                                .height(itemHeight),
                        textAlign = TextAlign.Center,
                        color = Black1,
                    )
                }
            }
        }
    }
}

fun selectedValue(state: LazyListState, list: List<String>): String {
    val extended = listOf(ZeroString, ZeroString) + list + listOf(ZeroString, ZeroString)
    val index = (state.firstVisibleItemIndex + 2).coerceIn(0, extended.lastIndex)
    return extended[index]
}