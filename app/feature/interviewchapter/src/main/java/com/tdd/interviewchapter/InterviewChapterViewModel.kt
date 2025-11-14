package com.tdd.interviewchapter

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.tdd.domain.entity.response.interview.InterviewChapterItem
import com.tdd.domain.entity.response.interview.InterviewChapterModel
import com.tdd.domain.entity.response.interview.InterviewSubChapterItem
import com.tdd.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.tdd.design_system.R
import com.tdd.domain.usecase.auth.GetFcmTokenUseCase
import com.tdd.ui.util.UriUtil
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class InterviewChapterViewModel @Inject constructor(
    private val getFcmTokenUseCase: GetFcmTokenUseCase
) : BaseViewModel<InterviewChapterPageState>(
    InterviewChapterPageState()
) {

    init {
        initSetChapterList()
        initGetFcmToken()
    }

    private fun initGetFcmToken() {
        viewModelScope.launch {
            getFcmTokenUseCase(Unit).collect { resultResponse(it, { data ->
                Timber.d("[테스트] -> $data")
            } )}
        }
    }

    private fun initSetChapterList() {
        // TODO 챕터 주제 서버통신
        val chapters = InterviewChapterModel(
            9,
            listOf(
                InterviewChapterItem(
                    1,
                    "1",
                    "성장 과정",
                    "나의 뿌리를 형성한 어린 시절부터 자라온 모든 시간의 기록입니다.",
                    listOf(
                        InterviewSubChapterItem(
                            9,
                            "1.1",
                            "출생",
                            "생의 시작, 내가 이 세상에 태어난 순간에 대한 이야기입니다",
                            R.drawable.ic_chapter_one_one
                        ),
                        InterviewSubChapterItem(
                            10,
                            "1.2",
                            "유년기",
                            "가장 순수했던 시절, 유년기의 소중한 기억들을 담았습니다.",
                            R.drawable.ic_chapter_one_two
                        ),
                        InterviewSubChapterItem(
                            11,
                            "1.3",
                            "발달",
                            "몸과 마음이 자라며 나를 형성해 온 성장의 발자취입니다.",
                            R.drawable.ic_chapter_one_three
                        )
                    )
                ),
                InterviewChapterItem(
                    2,
                    "2",
                    "가족 관계",
                    "가장 가까운 사람들이 남긴 따뜻한 온기와 사랑의 흔적들입니다.",
                    listOf(
                        InterviewSubChapterItem(
                            12,
                            "2.1",
                            "부모님",
                            "삶의 든든한 뿌리가 되어준 부모님과의 추억을 담았습니다."
                        ),
                    )
                ),
                InterviewChapterItem(
                    3,
                    "3",
                    "학창 시절",
                    "꿈을 키우고 배움을 쌓았던 소중한 학교 생활의 흔적입니다."
                ),
                InterviewChapterItem(
                    4,
                    "4",
                    "직업/진로",
                    "삶의 방향을 결정짓고 나를 성장시킨 일과 진로에 관한 이야기입니다."
                ),
                InterviewChapterItem(
                    5,
                    "5",
                    "가치관/성격",
                    "삶의 기준과 나를 움직이게 한 생각의 뿌리에 대해 돌아봅니다."
                ),
                InterviewChapterItem(
                    6,
                    "6",
                    "인생 경험",
                    "때로는 기쁘고, 때로는 아팠던 인생의 굴곡과 배움을 담았습니다."
                ),
                InterviewChapterItem(
                    7,
                    "7",
                    "감정/취향",
                    "내가 사랑한 것들, 마음을 움직인 감정과 취향의 조각들입니다."
                ),
                InterviewChapterItem(
                    8,
                    "8",
                    "사회/문화",
                    "시대와 사회, 문화를 바라보며 느낀 나의 생각과 삶의 배경입니다."
                ),
            )
        )

        updateState(
            uiState.value.copy(
                chapterList = chapters,
                progressChapter = findProgressChapter(chapters.currentChapterId, chapters.chapters)
            )
        )
    }

    private fun findProgressChapter(currentSubId: Int, chapters: List<InterviewChapterItem>): InterviewChapterItem =
        chapters.firstOrNull { chapter -> chapter.subChapters.any { it.chapterId == currentSubId } } ?: InterviewChapterItem()

    fun selectChapter(chapter: InterviewChapterItem) {
        updateState(
            uiState.value.copy(
                selectedChapter = chapter
            )
        )

        emitEventFlow(InterviewChapterEvent.ShowChapterBottomSheet)
    }

    fun setSelectedImg(context: Context, img: Uri?) {
        updateState(
            uiState.value.copy(
                selectedImg = img?.let { UriUtil.uriToFile(context, it) }
            )
        )
    }
}