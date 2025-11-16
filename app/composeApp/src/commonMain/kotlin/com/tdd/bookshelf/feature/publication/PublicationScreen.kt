package com.tdd.bookshelf.feature.publication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.img_chapter_detail
import com.tdd.bookshelf.core.designsystem.BackGround2
import com.tdd.bookshelf.core.designsystem.Black1
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Main1
import com.tdd.bookshelf.core.designsystem.PublicationBookDelete
import com.tdd.bookshelf.core.designsystem.PublicationBookWholeContent
import com.tdd.bookshelf.core.designsystem.PublicationTitle
import com.tdd.bookshelf.core.designsystem.Red1
import com.tdd.bookshelf.core.ui.common.button.UnderLineTextBtn
import com.tdd.bookshelf.core.ui.common.content.BasicDivider
import com.tdd.bookshelf.core.ui.common.content.ItemContentBox
import com.tdd.bookshelf.core.ui.common.content.TopBarContent
import com.tdd.bookshelf.core.ui.common.item.SelectCircleListItem
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyItemModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun PublicationScreen() {
    val viewModel: PublicationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    PublicationContent(
        interactionSource = interactionSource,
        autobiographyList = uiState.autobiographyList,
        selectedAutobiographyId = uiState.selectedAutobiographyId,
        onSelectAutobiographyId = { viewModel.setSelectedAutobiographyId(it) }
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
            iconVisible = false
        )

        PublicationAutobiographies(
            autobiographyList = autobiographyList,
            selectedId = selectedAutobiographyId,
            onSelect = onSelectAutobiographyId
        )

        BasicDivider()

        PublicationBookPreview(
            autobiography = autobiographyList.firstOrNull { it.autobiographyId == selectedAutobiographyId } ?: AllAutobiographyItemModel(),
            interactionSource = interactionSource
        )
    }
}

@Composable
private fun PublicationAutobiographies(
    autobiographyList: List<AllAutobiographyItemModel>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        itemsIndexed(autobiographyList) { index, item ->
            SelectCircleListItem(
                itemImg = Res.drawable.img_chapter_detail,
                itemText = item.title,
                isSelected = (selectedId == item.autobiographyId),
                onSelect = { onSelect(item.autobiographyId) }
            )
        }
    }
}

@Composable
private fun PublicationBookPreview(
    autobiography: AllAutobiographyItemModel,
    interactionSource: MutableInteractionSource,
) {
    Column {
        Text(
            text = autobiography.title,
            color = Black1,
            style = BookShelfTypo.Head3,
            modifier = Modifier
                .padding(top = 40.dp, start = 20.dp)
        )

        UnderLineTextBtn(
            interactionSource = interactionSource,
            textContent = PublicationBookWholeContent,
            textColor = Main1,
            onClick = {},
            paddingEnd = 58
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        PublicationBookPreviewContent(
            title = autobiography.title,
            content = autobiography.contentPreview,
            bookImg = autobiography.coverImageUrl,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.padding(top = 40.dp))

        UnderLineTextBtn(
            interactionSource = interactionSource,
            textContent = PublicationBookDelete,
            textColor = Red1,
            onClick = {},
            paddingEnd = 20
        )

        Spacer(modifier = Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun PublicationBookPreviewContent(
    title: String,
    content: String,
    bookImg: String? = "",
    modifier: Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth()
    ) { page ->
        PublicationBookItem(
            modifier = Modifier.fillMaxWidth(),
            isFirstPage = (page == 0),
            bookTitle = title,
            coverImage = bookImg,
            bookDetailText = content
        )
    }
}

@Composable
private fun PublicationBookItem(
    modifier: Modifier,
    isFirstPage: Boolean,
    bookTitle: String,
    coverImage: String? = "",
    bookDetailText: String
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
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(5.dp)),
                )
            } else {
                Text(
                    text = bookDetailText,
                    color = Black1,
                    style = BookShelfTypo.Body2,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                )
            }
        }
    )
}