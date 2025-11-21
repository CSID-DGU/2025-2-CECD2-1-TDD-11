package com.tdd.talktobook.feature.home

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger.Companion.d
import com.tdd.talktobook.core.ui.base.BaseViewModel
import com.tdd.talktobook.domain.entity.request.interview.InterviewSummariesRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsResponseModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.SubChapterItemModel
import com.tdd.talktobook.domain.entity.response.interview.InterviewSummariesListModel
import com.tdd.talktobook.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesChapterListUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCountMaterialsUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentInterviewProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentProgressAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCreateAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewSummariesUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostCreateInterviewQuestionUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.annotation.KoinViewModel
import kotlin.time.ExperimentalTime

@KoinViewModel
class HomeViewModel(
    private val getAutobiographiesChapterListUseCase: GetAutobiographiesChapterListUseCase,
    private val getAllAutobiographyUseCase: GetAllAutobiographyUseCase,
    private val getMemberInfoUseCase: GetMemberInfoUseCase,
    private val postCreateInterviewQuestionUseCase: PostCreateInterviewQuestionUseCase,
    private val postCreateAutobiographyUseCase: PostCreateAutobiographyUseCase,
    private val getCurrentProgressAutobiographyUseCase: GetCurrentProgressAutobiographyUseCase,
    private val getCurrentInterviewProgressUseCase: GetCurrentInterviewProgressUseCase,
    private val getCountMaterialsUseCase: GetCountMaterialsUseCase,
    private val getInterviewSummariesUseCase: GetInterviewSummariesUseCase,
) : BaseViewModel<HomePageState>(
    HomePageState(),
) {
    init {
//        initSetChapterList()
//        initSetAllAutobiography(GetAutobiographyType.DEFAULT)
//        initSetTodayDate()
//        initSetCreatedMaterials()
//        initSetAutobiographyProgress()
//        initSetMonthInterviewList()


        initSetTodayDate()
        initGetCurrentProgress()
    }

    private fun initSetTodayDate() {
        val today = uiState.value.today
        val todayDate =
            buildString {
                append(today.year.toString().padStart(4, '0'))
                append('.')
                append(today.monthNumber.toString().padStart(2, '0'))
                append('.')
                append(today.dayOfMonth.toString().padStart(2, '0'))
            }

        updateState(
            uiState.value.copy(
                selectedDate = todayDate,
                selectedDay = today.dayOfMonth,
            ),
        )
    }

    private fun initGetCurrentProgress() {
        viewModelScope.launch {
            getCurrentProgressAutobiographyUseCase(Unit).collect { resultResponse(it, ::onSuccessGetCurrent) }
        }
    }

    private fun onSuccessGetCurrent(data: CurrentProgressAutobiographyModel) {
        d("[ktor] homeViewmodel -> $data")
        when (data.isProgress) {
            true -> {
//                updateState(
//                    uiState.value.copy(
//                        currentAutobiographyId = data.autobiographyId,
//                        isCurrentProgress = true
//                    )
//                )
                setCurrentState(data)
            }

            false -> {
                updateState(
                    uiState.value.copy(isCurrentProgress = false)
                )
            }
        }
    }

    private fun setCurrentState(data: CurrentProgressAutobiographyModel) {
        updateState(
            uiState.value.copy(
                currentAutobiographyId = data.autobiographyId,
                isCurrentProgress = true
            )
        )

        initSetCreatedMaterials(data.autobiographyId)
        initSetInterviewProgress(data.autobiographyId)
        initSetMonthInterviewList(data.autobiographyId)
    }


    private fun initSetCreatedMaterials(autobiographyId: Int) {
        viewModelScope.launch {
            getCountMaterialsUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessCountMaterials)
            }
        }
    }

    private fun onSuccessCountMaterials(data: CountMaterialsResponseModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                createdMaterialList = data.popularMaterials,
            ),
        )
    }

    private fun initSetInterviewProgress(autobiographyId: Int) {
        viewModelScope.launch {
            getCurrentInterviewProgressUseCase(autobiographyId).collect {
                resultResponse(it, ::onSuccessInterviewProgress)
            }
        }
    }

    private fun onSuccessInterviewProgress(data: CurrentInterviewProgressModel) {
        d("[ktor] homeViewmodel -> $data")
        updateState(
            uiState.value.copy(
                autobiographyProgress = data.progressPercentage,
                currentAutobiographyStatus = data.status
            )
        )
    }

    private fun initSetMonthInterviewList(autobiographyId: Int) {
        val today = uiState.value.today

        viewModelScope.launch {
            getInterviewSummariesUseCase(InterviewSummariesRequestModel(autobiographyId, today.year, today.monthNumber)).collect { resultResponse(it, ::onSuccessGetMonthSummaries) }
        }
    }

    private fun onSuccessGetMonthSummaries(interviews: InterviewSummariesListModel) {
        d("[ktor] homeViewmodel -> ${interviews.interviews}")
        updateState(
            uiState.value.copy(
                monthInterviewList = interviews.interviews,
            ),
        )
    }

    @OptIn(ExperimentalTime::class)
    fun onClickInterviewDate(day: Int) {
        val today =
            Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
        val targetDate =
            LocalDate(
                year = today.year,
                monthNumber = today.monthNumber,
                dayOfMonth = day,
            )
        val selectedDate =
            buildString {
                append(targetDate.year.toString().padStart(4, '0'))
                append('.')
                append(targetDate.monthNumber.toString().padStart(2, '0'))
                append('.')
                append(targetDate.dayOfMonth.toString().padStart(2, '0'))
            }

        updateState(
            uiState.value.copy(
                selectedDay = day,
                selectedDate = selectedDate,
            ),
        )
    }

    // Legacy
//    private fun initSetChapterList() {
//        viewModelScope.launch {
//            getAutobiographiesChapterListUseCase(Unit).collect {
//                resultResponse(
//                    it,
//                    ::onSuccessGetChapterList,
//                )
//            }
//        }
//    }

//    private fun onSuccessGetChapterList(data: ChapterListModel) {
//        d("[ktor] homeViewmodel -> $data")
//        updateState(
//            uiState.value.copy(
//                chapterList = data.results,
//                subChapterList = if (data.results.isNotEmpty()) data.results[0].subChapters else emptyList(),
//                currentChapterId = data.currentChapterId,
//                currentChapter = setCurrentChapterItem(data.currentChapterId, data.results),
//            ),
//        )
//    }

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

//    private fun initSetAllAutobiography(type: GetAutobiographyType) {
//        viewModelScope.launch {
//            getAllAutobiographyUseCase(Unit).collect {
//                resultResponse(
//                    it,
//                    { data -> onSuccessAllAutobiography(data, type) },
//                )
//            }
//        }
//    }

//    private fun onSuccessAllAutobiography(
//        data: AllAutobiographyListModel,
//        type: GetAutobiographyType,
//    ) {
//        updateState(
//            uiState.value.copy(
//                allAutobiography = data,
//                allAutobiographyList = data.results,
//            ),
//        )
//
//        if (type == GetAutobiographyType.AfterCreate) emitEventFlow(HomeEvent.GoToDetailChapterPage)
//    }

    fun checkAutobiographyId() = uiState.value.allAutobiographyList.firstOrNull { it.chapterId == uiState.value.selectedDetailChapterId }?.autobiographyId ?: 0

//    fun setInterviewId(): Int {
//        val currentChapterId = uiState.value.currentChapterId
//        val interviewId =
//            uiState.value.allAutobiographyList.firstOrNull { it.chapterId == currentChapterId }?.interviewId
//                ?: 0
//
//        return interviewId
//    }

//    fun setAutobiographyId(chapterId: Int) {
//        val autobiographyId =
//            uiState.value.allAutobiographyList.firstOrNull { it.chapterId == chapterId }?.autobiographyId
//                ?: 0
//
//        setSelectedDetailChapterId(chapterId)
//
//        if (autobiographyId == 0) {
//            getMemberInfo()
//        } else {
//            emitEventFlow(HomeEvent.GoToDetailChapterPage)
//        }
//    }

//    private fun setSelectedDetailChapterId(chapterId: Int) {
//        updateState(
//            uiState.value.copy(
//                selectedDetailChapterId = chapterId,
//            ),
//        )
//    }

//    private fun getMemberInfo() {
//        viewModelScope.launch {
//            getMemberInfoUseCase(Unit).collect { resultResponse(it, ::onSuccessGetMemberInfo) }
//        }
//    }

//    private fun onSuccessGetMemberInfo(data: MemberInfoModel) {
//        d("[ktor] homeViewmodel -> $data")
//        updateState(
//            uiState.value.copy(
//                memberInfo = data,
//            ),
//        )
//
//        generateInterviewQuestions(data)
//    }

//    private fun generateInterviewQuestions(data: MemberInfoModel) {
//        val interviewQuestionRequest =
//            InterviewQuestionsRequestModel(
//                userInfo = data,
//                chapterInfo =
//                    ChapterInfoModel(
//                        uiState.value.chapterList[0].chapterName,
//                        uiState.value.chapterList[0].chapterDescription,
//                    ),
//                subChapterInfo =
//                    ChapterInfoModel(
//                        uiState.value.subChapterList[0].chapterName,
//                        uiState.value.subChapterList[0].chapterDescription,
//                    ),
//            )
//
//        postInterviewQuestions(interviewQuestionRequest)
//    }

//    private fun postInterviewQuestions(request: InterviewQuestionsRequestModel) {
//        viewModelScope.launch {
//            postCreateInterviewQuestionUseCase(request).collect {
//                resultResponse(
//                    it,
//                    ::onSuccessInterviewQuestions,
//                )
//            }
//        }
//    }

//    private fun onSuccessInterviewQuestions(data: InterviewQuestionsAIResponseModel) {
//        d("[ktor] homeViewmodel -> $data")
//        updateState(
//            uiState.value.copy(
//                interviewQuestions = data.interviewQuestions,
//            ),
//        )
//
//        createAutobiography(data.interviewQuestions)
//    }

//    private fun createAutobiography(questions: List<String>) {
//        val autobiography =
//            CreateAutobiographyRequestModel(
//                title = uiState.value.currentChapter.chapterName,
//                content = uiState.value.currentChapter.chapterDescription,
//                interviewQuestions = mapInterviewQuestionModel(questions),
//            )
//        postCreateAutobiography(autobiography)
//    }

//    private fun postCreateAutobiography(request: CreateAutobiographyRequestModel) {
//        viewModelScope.launch {
//            postCreateAutobiographyUseCase(request).collect {
//                resultResponse(it, { })
//            }
//
//            initSetAllAutobiography(GetAutobiographyType.AfterCreate)
//        }
//    }

//    private fun mapInterviewQuestionModel(questions: List<String>): List<InterviewQuestionModel> {
//        val interviewQuestionModels =
//            questions.mapIndexed { index, question ->
//                InterviewQuestionModel(index, question)
//            }
//
//        return interviewQuestionModels
//    }
}
