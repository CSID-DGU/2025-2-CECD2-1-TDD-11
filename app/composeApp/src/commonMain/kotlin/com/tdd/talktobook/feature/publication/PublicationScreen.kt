package com.tdd.talktobook.feature.publication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import talktobook.composeapp.generated.resources.Res
import com.tdd.talktobook.core.designsystem.BackGround2
import com.tdd.talktobook.core.designsystem.Black1
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Gray5
import com.tdd.talktobook.core.designsystem.Main1
import com.tdd.talktobook.core.designsystem.PublicationBookDelete
import com.tdd.talktobook.core.designsystem.PublicationBookWholeContent
import com.tdd.talktobook.core.designsystem.PublicationNotCreatedAutobiography
import com.tdd.talktobook.core.designsystem.PublicationTitle
import com.tdd.talktobook.core.designsystem.Red1
import com.tdd.talktobook.core.ui.common.button.UnderLineTextBtn
import com.tdd.talktobook.core.ui.common.content.BasicDivider
import com.tdd.talktobook.core.ui.common.content.ItemContentBox
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.core.ui.common.item.SelectCircleListItem
import com.tdd.talktobook.core.ui.util.paginateText
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyItemModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import talktobook.composeapp.generated.resources.img_chapter_detail
import talktobook.composeapp.generated.resources.img_empty_box

@Composable
internal fun PublicationScreen() {
    val viewModel: PublicationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    PublicationContent(
        interactionSource = interactionSource,
        autobiographyList = uiState.autobiographyList,
        selectedAutobiographyId = uiState.selectedAutobiographyId,
        onSelectAutobiographyId = { viewModel.setSelectedAutobiographyId(it) },
    )
}

@Composable
private fun PublicationContent(
    interactionSource: MutableInteractionSource,
    autobiographyList: List<AllAutobiographyItemModel>,
    selectedAutobiographyId: Int,
    onSelectAutobiographyId: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround2),
    ) {
        TopBarContent(
            content = PublicationTitle,
            interactionSource = interactionSource,
            iconVisible = false,
        )

        if (autobiographyList.isNotEmpty()) {
            SetAutobiographies(
                interactionSource = interactionSource,
                autobiographyList = autobiographyList,
                selectedAutobiographyId = selectedAutobiographyId,
                onSelectAutobiographyId = onSelectAutobiographyId,
            )
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize(),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(Res.drawable.img_empty_box),
                    contentDescription = "empty list",
                    modifier =
                        Modifier
                            .size(100.dp)
                            .padding(bottom = 17.dp)
                            .align(Alignment.CenterHorizontally),
                )

                Text(
                    text = PublicationNotCreatedAutobiography,
                    color = Gray5,
                    style = BookShelfTypo.Caption3,
                    modifier =
                        Modifier
                            .align(Alignment.CenterHorizontally),
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SetAutobiographies(
    interactionSource: MutableInteractionSource,
    autobiographyList: List<AllAutobiographyItemModel>,
    selectedAutobiographyId: Int,
    onSelectAutobiographyId: (Int) -> Unit,
) {
    PublicationAutobiographies(
        autobiographyList = autobiographyList,
        selectedId = selectedAutobiographyId,
        onSelect = onSelectAutobiographyId,
    )

    BasicDivider()

    PublicationBookPreview(
        autobiography = autobiographyList.firstOrNull { it.autobiographyId == selectedAutobiographyId } ?: AllAutobiographyItemModel(),
        interactionSource = interactionSource,
    )
}

@Composable
private fun PublicationAutobiographies(
    autobiographyList: List<AllAutobiographyItemModel>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
) {
    LazyRow(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        itemsIndexed(autobiographyList) { index, item ->
            SelectCircleListItem(
                itemImg = Res.drawable.img_chapter_detail,
                itemText = item.title,
                isSelected = (selectedId == item.autobiographyId),
                onSelect = { onSelect(item.autobiographyId) },
            )
        }
    }
}

@Composable
private fun PublicationBookPreview(
    autobiography: AllAutobiographyItemModel,
    interactionSource: MutableInteractionSource,
) {
    var totalPages by remember { mutableStateOf(1) }
    var currentPage by remember { mutableStateOf(1) }

    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = autobiography.title,
                color = Black1,
                style = BookShelfTypo.Head3,
                modifier =
                    Modifier
                        .weight(1f),
            )

            Text(
                text = "$currentPage/$totalPages",
                color = Black1,
                style = BookShelfTypo.Caption4,
            )
        }

        UnderLineTextBtn(
            interactionSource = interactionSource,
            textContent = PublicationBookWholeContent,
            textColor = Main1,
            onClick = {},
            paddingEnd = 58,
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        PublicationBookPreviewContent(
            autobiographyId = autobiography.autobiographyId,
            title = autobiography.title,
            content = autobiography.contentPreview,
            bookImg = autobiography.coverImageUrl,
            modifier = Modifier.weight(1f),
            onSetTotalPages = { totalPages = it },
            onSetCurrentPage = { currentPage = it },
        )

        Spacer(modifier = Modifier.padding(top = 40.dp))

        UnderLineTextBtn(
            interactionSource = interactionSource,
            textContent = PublicationBookDelete,
            textColor = Red1,
            onClick = {},
            paddingEnd = 20,
        )

        Spacer(modifier = Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun PublicationBookPreviewContent(
    autobiographyId: Int,
    title: String,
    content: String,
    bookImg: String? = "",
    modifier: Modifier,
    onSetTotalPages: (Int) -> Unit,
    onSetCurrentPage: (Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val autobiographyTextStyle = BookShelfTypo.Body2
    val density = LocalDensity.current

    var containerSize by remember { mutableStateOf<IntSize?>(null) }
    var pages by remember { mutableStateOf<List<String>>(emptyList()) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    containerSize = coordinates.size
                },
    ) {
        val size = containerSize

        LaunchedEffect(content, size, autobiographyTextStyle) {
            if (size != null) {
                with(density) {
                    val horizontalPaddingPx = (50.dp + 50.dp + 20.dp + 20.dp).roundToPx()
                    val verticalPaddingPx = (15.dp * 2).roundToPx()

                    val maxWidthPx = (size.width - horizontalPaddingPx).coerceAtLeast(0)
                    val maxHeightPx = (size.height - verticalPaddingPx).coerceAtLeast(0)

                    pages =
                        paginateText(
                            fullText = content,
                            textMeasurer = textMeasurer,
                            maxWidthPx = maxWidthPx,
                            maxHeightPx = maxHeightPx,
                            textStyle = autobiographyTextStyle,
                        )
                }
            }
        }

        if (size != null && pages.isNotEmpty()) {
            val pagerState = rememberPagerState(pageCount = { pages.size + 1 })

            LaunchedEffect(autobiographyId, pages.size) {
                pagerState.scrollToPage(0)
                onSetTotalPages(pages.size + 1)
            }

            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .collect { pageIndex ->
                        onSetCurrentPage(pageIndex + 1)
                    }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                PublicationBookItem(
                    modifier = Modifier.fillMaxSize(),
                    isFirstPage = (page == 0),
                    bookTitle = title,
                    coverImage = bookImg,
                    bookDetailText = if (page != 0) pages[page - 1] else Empty,
                )
            }
        }
    }
}

@Composable
private fun PublicationBookItem(
    modifier: Modifier,
    isFirstPage: Boolean,
    bookTitle: String,
    coverImage: String? = "",
    bookDetailText: String,
) {
    ItemContentBox(
        modifier = modifier,
        paddingStart = 50,
        paddingEnd = 50,
        content = {
            if (isFirstPage) {
                Image(
                    painter = painterResource(Res.drawable.img_chapter_detail),
                    contentDescription = "autobiography cover",
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(5.dp)),
                )
            } else {
                Text(
                    text = bookDetailText,
                    color = Black1,
                    style = BookShelfTypo.Body2,
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp, vertical = 15.dp),
                )
            }
        },
    )
}
