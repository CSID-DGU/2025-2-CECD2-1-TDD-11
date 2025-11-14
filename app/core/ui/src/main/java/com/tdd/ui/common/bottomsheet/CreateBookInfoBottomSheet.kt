package com.tdd.ui.common.bottomsheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BackGround
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.BottomSheet
import com.tdd.design_system.Main2
import com.tdd.design_system.Main3
import com.tdd.design_system.ProgressBottomSheetBtn
import com.tdd.design_system.ProgressBottomSheetTitle
import com.tdd.design_system.R
import com.tdd.domain.entity.response.progress.ProgressBookInfoModel
import com.tdd.ui.common.button.BottomRectangleBtn

@Composable
fun CreateBookInfoBottomSheet(
    onClickClose: () -> Unit,
    bookInfo: ProgressBookInfoModel,
    onClickCreateBtn: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    CreateBookInfoContent(
        interactionSource = interactionSource,
        onClickClose = onClickClose,
        bookInfo = bookInfo,
        onClickCreateBtn = onClickCreateBtn
    )
}

@Composable
fun CreateBookInfoContent(
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClickClose: () -> Unit = {},
    bookInfo: ProgressBookInfoModel = ProgressBookInfoModel(),
    onClickCreateBtn: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackGround)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ProgressBottomSheetTitle,
                    color = Main3,
                    style = BookShelfTypo.head30,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                CreateBookInfoBox(
                    bookInfo = bookInfo,
                    modifier = Modifier.weight(1f)
                )
            }

            BottomRectangleBtn(
                btnTextContent = ProgressBottomSheetBtn,
                isBtnActivated = true,
                onClickAction = onClickCreateBtn
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
fun CreateBookInfoBox(
    bookInfo: ProgressBookInfoModel,
    modifier: Modifier,
) {
    Box(
//        modifier = modifier
//            .padding(vertical = 20.dp)
//            .fillMaxWidth()
        modifier = modifier
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_ticket_example2),
            contentDescription = "background example",
            modifier = Modifier
                .fillMaxSize()
        )

//        Image(
//            painter = painterResource(id = R.drawable.ic_ticket),
//            contentDescription = "background",
//            modifier = Modifier
//                .fillMaxSize()
////                .width(450.dp)
//        )
//
//        Column(
//            modifier = Modifier
////                .size(width = 230.dp, height = 270.dp)
//                .width(320.dp)
////                .fillMaxSize()
//            ,
//            horizontalAlignment = Alignment.Start
//        ) {
//            Text(
//                text = "자서전 제목",
//                color = Main3,
//                style = BookShelfTypo.body50,
//                modifier = Modifier
//                    .padding(top = 30.dp, start = 30.dp)
//            )
//
//            Text(
//                text = bookInfo.bookTitle,
//                color = Black1,
//                style = BookShelfTypo.caption40,
//                modifier = Modifier
//                    .padding(top = 6.dp, start = 15.dp)
//            )
//
//            Text(
//                text = "출판 예상 시간",
//                color = Main3,
//                style = BookShelfTypo.body50,
//                modifier = Modifier
//                    .padding(top = 30.dp, start = 15.dp)
//            )
//
//            Row(
//                modifier = Modifier
//                    .padding(start = 15.dp, top = 6.dp)
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.ic_airplane),
//                    contentDescription = "airplane",
//                    modifier = Modifier.size(60.dp)
//                )
//            }
//
//            Row(
//                modifier = Modifier
//                    .padding(vertical = 30.dp, horizontal = 15.dp)
//                    .fillMaxWidth()
//                    .align(Alignment.CenterHorizontally),
//                horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally)
//            ) {
//                CreateBookInfoDetailBox(
//                    title = "페이지 수",
//                    content = String.format(BookInfoPages, bookInfo.page),
//                    detailIcon = R.drawable.ic_book_pages
//                )
//
//                CreateBookInfoDetailBox(
//                    title = "예상 가격",
//                    content = String.format(BookInfoPrice, bookInfo.price),
//                    detailIcon = R.drawable.ic_book_price
//                )
//            }
//
////            Spacer(modifier = Modifier.weight(1f))
//
//            Text(
//                text = ProgressBottomSheetNotice,
//                color = Black1,
//                style = BookShelfTypo.caption40,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(bottom = 15.dp)
//            )
//        }
    }
}

@Composable
fun CreateBookInfoDetailBox(
    title: String,
    content: String,
    detailIcon: Int,
) {
    Column(
        modifier = Modifier
            .background(BottomSheet)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = 10.dp, start = 6.dp)
        ) {
            Image(
                painter = painterResource(id = detailIcon),
                contentDescription = "detail",
                modifier = Modifier
                    .size(20.dp)
            )

            Text(
                text = title,
                color = Main3,
                style = BookShelfTypo.body50
            )
        }

        Text(
            text = content,
            color = Main2,
            style = BookShelfTypo.caption40,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 20.dp, top = 10.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewCreateBookInfo() {
    CreateBookInfoContent()
}