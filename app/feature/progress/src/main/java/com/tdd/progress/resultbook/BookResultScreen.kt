package com.tdd.progress.resultbook

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BackGround
import com.tdd.design_system.Black1
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray5
import com.tdd.design_system.R

@Composable
fun BookResultScreen(
    goBack: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackGround)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_book_example2),
                contentDescription = "book background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            Text(
                text = "혼란을 건너 성장으로",
                color = Black1,
                style = BookShelfTypo.head20,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 20.dp)
            )

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
                        onClick = { goBack() }
                    )
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {

                Text(
                    text = "1. 어린 시절",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "어릴 때 저는 정말 호기심이 많은 아이였어요. 뭐든지 \"왜?\"라고 물어보는 게 습관이었는데, 부모님께서는 항상 친절하게 설명해주셨어요. 유치원에서는 친구들과 소꿉놀이하는 걸 좋아했고, 특히 선생님 놀이를 할 때 항상 선생님 역할을 맡았던 기억이 나요.\n" +
                            "\n" +
                            "초등학교 때는 그림 그리기와 책 읽기를 정말 좋아했어요. 도서관에서 하루 종일 책을 읽다가 집에 늦게 들어가서 엄마한테 혼나기도 했죠. 그때부터 글쓰기에도 관심이 생겨서 일기를 쓰기 시작했어요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                )

                Text(
                    text = "2. 사춘기의 혼란",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "중학교에 올라가면서 많은 변화가 있었어요. 갑자기 키도 크고, 마음도 복잡해지고... 특히 친구 관계에서 스트레스를 많이 받았어요. 그때는 부모님과도 자주 부딪혔는데, 지금 생각해보면 정말 철없었던 것 같아요.\n" +
                            "\n" +
                            "중2 때 담임선생님께서 \"지민아, 너는 생각이 깊은 아이구나\"라고 말씀해주셨는데, 그 말이 정말 큰 위로가 되었어요. 그때부터 제자신에 대해 조금씩 이해하기 시작한 것 같아요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                )

                Text(
                    text = "3. 입시의 압박",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "고등학교는 정말 힘들었어요. 매일 새벽까지 공부하고, 주말에도 학원 다니고. 친구들과 놀 시간도 없었고, 스트레스로 머리카락도 많이 빠졌어요.\n" +
                            "\n" +
                            "특히 고3 때는 정말 지옥 같았는데, 그때 엄마가 매일 간식을 챙겨주시고 \"우리 딸 파이팅!\"이라고 응원해주셔서 버틸 수 있었어요. 수능 끝나고 결과 나올 때까지 정말 불안했는데, 다행히 원하던 대학에 합격해서 가족들과 함께 울면서 기뻐했어요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                )

                Text(
                    text = "4. 대학생활의 새로운 세상",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "대학에 와서 정말 많은 걸 경험했어요. 처음으로 부모님과 떨어져 기숙사 생활을 시작했는데, 처음엔 정말 외롭고 힘들었어요. 하지만 룸메이트 언니가 정말 좋은 분이셔서 금세 적응할 수 있었어요.\n" +
                            "\n" +
                            "1학년 때는 동아리도 가입하고, 대학 축제도 참여하고, 정말 신나게 보냈어요. 고등학교 때와는 완전히 다른 자유로운 분위기가 너무 좋았어요.\n" +
                            "\n" +
                            "2학년부터는 아르바이트도 시작했어요. 카페에서 일하면서 다양한 사람들을 만나고, 돈을 벌어보는 경험도 해봤어요. 처음엔 서툴렀지만 점점 능숙해지면서 자신감도 생겼어요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                )

                Text(
                    text = "5. 현재의 고민들",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "지금 3학년이 되면서 취업에 대한 고민이 정말 많아요. 주변 친구들은 다들 토익 공부하고, 자격증 따고, 인턴십 지원하고, 저도뭔가 해야 할 것 같은데 아직 확실하게 정하지 못했어요.\n" +
                            "\n" +
                            "부모님께서는 \"천천히 생각해봐도 된다\"고 말씀하시지만, 솔직히 조급한 마음도 들어요. 하지만 제가 정말 좋아하고 잘할 수 있는일을 찾고 싶어요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
                )

                Text(
                    text = "6. 나의 성장과 꿈",
                    color = Black1,
                    style = BookShelfTypo.head30,
                    modifier = Modifier
                        .padding(top = 30.dp, start = 30.dp)
                )

                Text(
                    text = "돌이켜보면 정말 많은 변화가 있었어요. 어릴 때의 순수함에서 시작해서, 사춘기의 혼란을 거쳐, 입시의 압박을 이겨내고, 지금은성인으로서 제 길을 찾아가고 있어요.\n" +
                            "\n" +
                            "실수도 많이 했고, 후회되는 일들도 있지만, 그 모든 게 지금의 저를 만들어준 소중한 경험이라고 생각해요.\n" +
                            "\n" +
                            "앞으로는 더 많은 사람들과 소통하고, 제가 가진 재능을 발휘할 수 있는 일을 찾고 싶어요. 그리고 언젠가는 저도 누군가에게 도움이 되는 사람이 되고 싶어요.",
                    color = Gray5,
                    style = BookShelfTypo.body30,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 30.dp, end = 30.dp, bottom = 50.dp)
                )

//                Text(
//                    text = "7. 감정/취향",
//                    color = Black1,
//                    style = BookShelfTypo.head30,
//                    modifier = Modifier
//                        .padding(top = 30.dp, start = 30.dp)
//                )
//
//                Text(
//                    text = "나는 어릴 적부터 감성적인 면이 강했다. 작은 일에도 쉽게 감동하고, 아름다운 것을 보면 눈물을 글썽이곤 했다. 이러한 감수성은 나에게 문학을 사랑하고, 예술을 즐기는 취향을 선물해주었다. 바흐의 무반주 첼로 모음곡을 들으며 위안을 얻고, 고흐의 그림을 보며 삶의 에너지를 느낀다. 조용히 혼자 생각에 잠기는 것을 좋아하며, 이때 주로 나의 감정과 생각들을 글로 정리하곤 한다. 이는 나의 내면의 소리에 귀 기울이고, 복잡한 감정들을 다스리는 나만의 방법이다. 최근에는 정원 가꾸는 취미를 시작했다. 작은 씨앗에서 싹이 트고, 꽃이 피는 과정을 지켜보는 것은 나에게 큰 평온함과 기쁨을 준다. 자연의 순리를 따르며 성장하는 식물들을 보며 나는 삶의 지혜를 얻기도 한다. 또한, 맛있는 음식을 만들어 소중한 사람들과 나누는 것을 즐긴다. 요리는 나에게 창의적인 즐거움과 함께 사랑을 표현하는 방식이 된다. 이러한 취향들은 나의 삶을 더욱 풍요롭게 만들고, 나 자신을 더 깊이 이해하는 데 도움을 준다.",
//                    color = Gray5,
//                    style = BookShelfTypo.body30,
//                    modifier = Modifier
//                        .padding(top = 15.dp, start = 30.dp, end = 30.dp)
//                )
//
//                Text(
//                    text = "8. 사회/문화",
//                    color = Black1,
//                    style = BookShelfTypo.head30,
//                    modifier = Modifier
//                        .padding(top = 30.dp, start = 30.dp)
//                )
//
//                Text(
//                    text = "나는 나의 직업을 통해 사회에 기여하는 것에 큰 의미를 둔다. 내가 만든 책들이 독자들에게 새로운 지식과 영감을 주고, 더 나은 세상을 만드는 데 조금이나마 도움이 되기를 바랐다. 특히 소외된 이웃들의 이야기를 담은 책들을 출판하며, 그들의 목소리가 세상에 전달될 수 있도록 노력했다. 은퇴 후에는 지역 사회의 작은 도서관에서 자원봉사를 시작했다. 아이들에게 책을 읽어주고, 고령자들에게 디지털 기기 사용법을 알려주는 활동을 하며 여생의 보람을 느낀다. 빠르게 변화하는 디지털 문화 속에서 고령자들이 소외되지 않고 새로운 기술을 통해 삶을 풍요롭게 할 수 있도록 돕는 것이 나의 작은 목표이다. 또한, K-POP, K-드라마 등 한국 문화가 전 세계적으로 사랑받는 모습을 보며 큰 자부심을 느낀다. 언어와 국경을 넘어 문화로 소통하는 시대에 살고 있다는 것이 감사하다. 나는 앞으로도 세상과 소통하고 배우며, 내가 가진 작은 지식과 경험을 나누어 사회에 긍정적인 발자취를 남기고 싶다. 삶의 마지막 순간까지 배우고 성장하며, 사랑하는 이들과 함께 이 세상의 아름다움을 만끽하고 싶다.",
//                    color = Gray5,
//                    style = BookShelfTypo.body30,
//                    modifier = Modifier
//                        .padding(top = 15.dp, start = 30.dp, end = 30.dp, bottom = 50.dp)
//                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBookResult() {
    BookResultScreen()
}