package com.tdd.bookshelf.feature.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.bookshelf.core.ui.base.BaseViewModel
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.bookshelf.domain.entity.request.autobiography.InterviewQuestionModel
import com.tdd.bookshelf.domain.entity.request.interview.ai.InterviewQuestionsRequestModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterInfoModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterListModel
import com.tdd.bookshelf.domain.entity.response.autobiography.SubChapterItemModel
import com.tdd.bookshelf.domain.entity.response.interview.ai.InterviewQuestionsAIResponseModel
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel
import com.tdd.bookshelf.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import com.tdd.bookshelf.domain.usecase.autobiograph.GetAutobiographiesChapterListUseCase
import com.tdd.bookshelf.domain.usecase.autobiograph.PostCreateAutobiographyUseCase
import com.tdd.bookshelf.domain.usecase.interview.ai.PostCreateInterviewQuestionUseCase
import com.tdd.bookshelf.domain.usecase.member.GetMemberInfoUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val getAutobiographiesChapterListUseCase: GetAutobiographiesChapterListUseCase,
    private val getAllAutobiographyUseCase: GetAllAutobiographyUseCase,
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val postCreateInterviewQuestionUseCase: PostCreateInterviewQuestionUseCase,
    private val postCreateAutobiographyUseCase: PostCreateAutobiographyUseCase,
) : BaseViewModel<HomePageState>(
        HomePageState(),
    ) {
    init {
        initSetChapterList()
        initSetAllAutobiography(GetAutobiographyType.DEFAULT)
    }

    private fun initSetChapterList() {
        viewModelScope.launch {
            getAutobiographiesChapterListUseCase(Unit).collect {
                resultResponse(
                    it,
                    ::onSuccessGetChapterList,
                )
            }
        }
    }

    private fun onSuccessGetChapterList(data: ChapterListModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                chapterList = data.results,
                subChapterList = if (data.results.isNotEmpty()) data.results[0].subChapters else emptyList(),
                currentChapterId = data.currentChapterId,
                currentChapter = setCurrentChapterItem(data.currentChapterId, data.results),
            ),
        )
    }

    private fun setCurrentChapterItem(
        currentChapterId: Int,
        chapters: List<ChapterItemModel>,
    ): SubChapterItemModel {
        var currentChapter: SubChapterItemModel

        chapters.firstOrNull { it.chapterId == currentChapterId }?.let { chapterItem ->
            currentChapter =
                SubChapterItemModel(
                    chapterItem.chapterId,
                    chapterItem.chapterNumber,
                    chapterItem.chapterName,
                    chapterItem.chapterDescription,
                    chapterItem.chapterCreatedAt,
                )

            return currentChapter
        }

        for (chapter in chapters) {
            val subChapter = chapter.subChapters.firstOrNull { it.chapterId == currentChapterId }
            if (subChapter != null) return subChapter
        }

        return SubChapterItemModel()
    }

    private fun initSetAllAutobiography(type: GetAutobiographyType) {
        viewModelScope.launch {
            getAllAutobiographyUseCase(Unit).collect {
                resultResponse(
                    it,
                    { data -> onSuccessAllAutobiography(data, type) },
                )
            }
        }
    }

    private fun onSuccessAllAutobiography(
        data: AllAutobiographyListModel,
        type: GetAutobiographyType,
    ) {
        updateState(
            uiState.value.copy(
                allAutobiography = data,
                allAutobiographyList = data.results,
            ),
        )

        if (type == GetAutobiographyType.AfterCreate) emitEventFlow(HomeEvent.GoToDetailChapterPage)
    }

    fun checkAutobiographyId() = uiState.value.allAutobiographyList.firstOrNull { it.chapterId == uiState.value.selectedDetailChapterId }?.autobiographyId ?: 0

    fun setInterviewId(): Int {
        val currentChapterId = uiState.value.currentChapterId
        val interviewId =
            uiState.value.allAutobiographyList.firstOrNull { it.chapterId == currentChapterId }?.interviewId
                ?: 0

        return interviewId
    }

    fun setAutobiographyId(chapterId: Int) {
        val autobiographyId =
            uiState.value.allAutobiographyList.firstOrNull { it.chapterId == chapterId }?.autobiographyId
                ?: 0

        setSelectedDetailChapterId(chapterId)

        if (autobiographyId == 0) {
            getMemberInfo()
        } else {
            emitEventFlow(HomeEvent.GoToDetailChapterPage)
        }
    }

    private fun setSelectedDetailChapterId(chapterId: Int) {
        updateState(
            uiState.value.copy(
                selectedDetailChapterId = chapterId,
            ),
        )
    }

    private fun getMemberInfo() {
        viewModelScope.launch {
            getMemberInfoUseCase(Unit).collect { resultResponse(it, ::onSuccessGetMemberInfo) }
        }
    }

    private fun onSuccessGetMemberInfo(data: MemberInfoModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                memberInfo = data,
            ),
        )

        generateInterviewQuestions(data)
    }

    private fun generateInterviewQuestions(data: MemberInfoModel) {
        val interviewQuestionRequest =
            InterviewQuestionsRequestModel(
                userInfo = data,
                chapterInfo =
                    ChapterInfoModel(
                        uiState.value.chapterList[0].chapterName,
                        uiState.value.chapterList[0].chapterDescription,
                    ),
                subChapterInfo =
                    ChapterInfoModel(
                        uiState.value.subChapterList[0].chapterName,
                        uiState.value.subChapterList[0].chapterDescription,
                    ),
            )

        postInterviewQuestions(interviewQuestionRequest)
    }

    private fun postInterviewQuestions(request: InterviewQuestionsRequestModel) {
        viewModelScope.launch {
            postCreateInterviewQuestionUseCase(request).collect {
                resultResponse(
                    it,
                    ::onSuccessInterviewQuestions,
                )
            }
        }
    }

    private fun onSuccessInterviewQuestions(data: InterviewQuestionsAIResponseModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                interviewQuestions = data.interviewQuestions,
            ),
        )

        createAutobiography(data.interviewQuestions)
    }

    private fun createAutobiography(questions: List<String>) {
        val autobiography =
            CreateAutobiographyRequestModel(
                title = uiState.value.currentChapter.chapterName,
                content = uiState.value.currentChapter.chapterDescription,
                interviewQuestions = mapInterviewQuestionModel(questions),
            )
        postCreateAutobiography(autobiography)
    }

    private fun postCreateAutobiography(request: CreateAutobiographyRequestModel) {
        viewModelScope.launch {
            postCreateAutobiographyUseCase(request).collect {
                resultResponse(it, { })
            }

            initSetAllAutobiography(GetAutobiographyType.AfterCreate)
        }
    }

    private fun mapInterviewQuestionModel(questions: List<String>): List<InterviewQuestionModel> {
        val interviewQuestionModels =
            questions.mapIndexed { index, question ->
                InterviewQuestionModel(index, question)
            }

        return interviewQuestionModels
    }
}
