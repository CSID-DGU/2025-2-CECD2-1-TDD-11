package com.tdd.interviewchapter

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.design_system.BackGround
import com.tdd.design_system.Black1
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray5
import com.tdd.design_system.InterviewChapterTitle
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.interview.InterviewChapterModel
import com.tdd.ui.common.content.TopPageTitle
import com.tdd.ui.common.type.ChapterType

@Composable
fun InterviewChapterScreen(
    showChapterBottomSheet: (Int, InterviewChapterItem) -> Unit,
) {

    val viewModel: InterviewChapterViewModel = hiltViewModel()
    val uiState: InterviewChapterPageState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
            viewModel.setSelectedImg(context, uri)
        }
    )
    val bitmap = imageUri?.let {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            ImageDecoder.decodeBitmap(source)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is InterviewChapterEvent.ShowChapterBottomSheet -> {
                    showChapterBottomSheet(
                        uiState.chapterList.currentChapterId,
                        uiState.selectedChapter
                    )
                }
            }
        }
    }

    InterviewChapterContent(
        interactionSource = interactionSource,
        chapterList = uiState.chapterList,
        progressChapter = uiState.progressChapter,
        onClickChapter = {
//            viewModel.selectChapter(it)
            launcher.launch("image/*")
        },
        selectedImg = bitmap
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterviewChapterContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    chapterList: InterviewChapterModel = InterviewChapterModel(),
    progressChapter: InterviewChapterItem = InterviewChapterItem(),
    onClickChapter: (InterviewChapterItem) -> Unit = {},
    selectedImg: Bitmap? = null,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround)
    ) {
        val itemSpacing = 40.dp
        val itemWidth = 160.dp

        val itemsPerRow = if (maxWidth < 600.dp) 2 else 4
        val totalSpacing = itemSpacing * (itemsPerRow - 1)
        val totalContentWidth = itemWidth * itemsPerRow + totalSpacing

        Column {
            TopPageTitle(title = InterviewChapterTitle)

            FlowRow(
                modifier = Modifier
                    .widthIn(max = totalContentWidth)
                    .align(Alignment.CenterHorizontally)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
                verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically)
            ) {
                chapterList.chapters.forEach { chapter ->
                    InterviewChapterItem(
                        chapter = chapter,
                        modifier = Modifier.width(itemWidth),
                        isValid = (progressChapter.chapterId == chapter.chapterId),
                        isFinish = (chapter.chapterId < progressChapter.chapterId),
                        onClickAction = { onClickChapter(chapter) },
                        interactionSource = interactionSource,
                        selectedImg = selectedImg
                    )
                }
            }
        }
    }
}

@Composable
fun InterviewChapterItem(
    chapter: InterviewChapterItem,
    modifier: Modifier,
    isValid: Boolean,
    isFinish: Boolean,
    onClickAction: () -> Unit,
    interactionSource: MutableInteractionSource,
    selectedImg: Bitmap?,
) {
    Column(
        modifier = modifier
            .clickable(
                enabled = (isValid || isFinish),
                onClick = onClickAction,
                indication = null,
                interactionSource = interactionSource
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
//        Image(
//            painter = painterResource(id = ChapterType.getChapterImg(chapter.chapterId)),
//            contentDescription = "chapter",
//            modifier = Modifier
//                .clip(RoundedCornerShape(8.dp))
//                .size(width = 160.dp, height = 180.dp)
//                .alpha(if (isValid) 1.0f else 0.6f),
//            contentScale = ContentScale.Crop
//        )
        if (chapter.chapterId == 1) {
            if (selectedImg == null) {
                Image(
                    painter = painterResource(id = ChapterType.getChapterImg(chapter.chapterId)),
                    contentDescription = "chapter",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(width = 160.dp, height = 180.dp)
                        .alpha(if (isValid) 1.0f else 0.6f),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    bitmap = selectedImg.asImageBitmap(),
                    contentDescription = "selected image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .size(width = 160.dp, height = 180.dp)
                        .alpha(if (isValid) 1.0f else 0.6f),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Image(
                painter = painterResource(id = ChapterType.getChapterImg(chapter.chapterId)),
                contentDescription = "chapter",
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .size(width = 160.dp, height = 180.dp)
                    .alpha(if (isValid) 1.0f else 0.6f),
                contentScale = ContentScale.Crop
            )
        }

//        if (selectedImg == null) {
//            Image(
//                painter = painterResource(id = ChapterType.getChapterImg(chapter.chapterId)),
//                contentDescription = "chapter",
//                modifier = Modifier
//                    .clip(RoundedCornerShape(8.dp))
//                    .size(width = 160.dp, height = 180.dp)
//                    .alpha(if (isValid) 1.0f else 0.6f),
//                contentScale = ContentScale.Crop
//            )
//        } else {
//            Image(
//                bitmap = selectedImg.asImageBitmap(),
//                contentDescription = "selected image",
//                modifier = Modifier
//                    .clip(RoundedCornerShape(8.dp))
//                    .size(width = 160.dp, height = 180.dp)
//                    .alpha(if (isValid) 1.0f else 0.6f),
//                contentScale = ContentScale.Crop
//            )
//        }


        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = chapter.chapterName,
            color = if (isValid) Black1 else Gray5,
            style = BookShelfTypo.body30
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewInterviewChapter() {
    InterviewChapterContent()
}