package com.tdd.ui.common.bottomsheet

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BackGround
import com.tdd.design_system.Black1
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.ChapterFinishCheck
import com.tdd.design_system.ChapterProgressCheck
import com.tdd.design_system.ChapterProgressTitle
import com.tdd.design_system.ChapterSubTitle
import com.tdd.design_system.Gray4
import com.tdd.design_system.Gray5
import com.tdd.design_system.Main3
import com.tdd.design_system.R
import com.tdd.design_system.White4
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.interview.InterviewSubChapterItem

@Composable
fun ChapterBottomSheet(
    selectedChapter: InterviewChapterItem,
    currentId: Int,
    onClickClose: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    ChapterBottomSheetContent(
        chapter = selectedChapter,
        currentId = currentId,
        onClickClose = onClickClose,
        interactionSource = interactionSource
    )
}

@Composable
fun ChapterBottomSheetContent(
    chapter: InterviewChapterItem = InterviewChapterItem(),
    currentId: Int = 10,
    onClickClose: () -> Unit = {},
    interactionSource: MutableInteractionSource = MutableInteractionSource()
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackGround)
    ) {
        Column {
            Image(
                painter = painterResource(id = R.drawable.ic_chapter_one),
                contentDescription = "chapter background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text(
                text = chapter.chapterName,
                color = Main3,
                style = BookShelfTypo.body10,
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
            )

            Text(
                text = chapter.chapterDescription,
                color = Gray5,
                style = BookShelfTypo.body40,
                modifier = Modifier
                    .padding(top = 3.dp, start = 15.dp)
            )

            Divider(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .border(2.dp, color = White4)
            )

            ChapterBottomSheetDetail(
                subChapters = chapter.subChapters,
                currentId = currentId
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "close",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 15.dp, end = 15.dp)
                .size(45.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClickClose
                )
        )
    }
}

@Composable
fun ChapterBottomSheetDetail(
    subChapters: List<InterviewSubChapterItem>,
    currentId: Int,
) {
    LazyColumn {
        items(subChapters) { subChapter ->
            ChapterDetailItem(
                subChapter = subChapter,
                isProgress = (subChapter.chapterId == currentId),
                isFinish = (subChapter.chapterId < currentId)
            )

            Divider(modifier = Modifier.border(2.dp, color = White4))
        }
    }
}

@Composable
fun ChapterDetailItem(
    subChapter: InterviewSubChapterItem,
    isProgress: Boolean = false,
    isFinish: Boolean = false,
) {
    Row {
        Box(
            modifier = Modifier
                .padding(top = 30.dp, bottom = 30.dp, start = 30.dp, end = 20.dp)
                .size(100.dp)
        ) {
            Image(
                painter = painterResource(id = subChapter.chapterImg),
                contentDescription = "sub chapter",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxSize()
                    .alpha(if (isProgress || isFinish) 1.0f else 0.6f)
            )

            if (isProgress) {
                Image(
                    painter = painterResource(id = R.drawable.ic_progress),
                    contentDescription = "progress",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 30.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = String.format(
                    ChapterSubTitle,
                    subChapter.chapterNumber,
                    subChapter.chapterName
                ),
                color = Black1,
                style = BookShelfTypo.caption30
            )

            Text(
                text = subChapter.chapterDescription,
                color = Black1,
                style = BookShelfTypo.caption40,
                modifier = Modifier
                    .padding(top = 10.dp)
            )

            Text(
                text = if (isProgress) ChapterProgressCheck else ChapterFinishCheck,
                color = if (isFinish || isProgress) Gray4 else Color.Transparent,
                style = BookShelfTypo.body60.copy(
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .padding(top = 10.dp)
            )
        }

        if (isProgress) {
            Text(
                text = ChapterProgressTitle,
                color = Main3,
                style = BookShelfTypo.caption30,
                modifier = Modifier
                    .padding(end = 40.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChapterBottomSheet() {
    ChapterBottomSheetContent()
}