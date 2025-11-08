package com.tdd.navigation

sealed class NavRoutes(val route: String) {

    // OnBoarding Graph
    data object OnBoardingGraph: NavRoutes("onboarding_graph")
    data object UserAgeScreen: NavRoutes("user_age")
    data object UserGenderScreen: NavRoutes("user_gender")
    data object ScholarShipScreen: NavRoutes("scholarship")
    data object MarriageScreen: NavRoutes("marriage")
    data object OnBoardingScreen: NavRoutes("onboarding")

    // InterView Graph
    data object InterViewGraph: NavRoutes("interview_graph")
    data object StartInterViewScreen: NavRoutes("start_interview")
    data object InterviewScreen: NavRoutes("interview")
    data object InterviewMainScreen: NavRoutes("interview_main")

    // Interview Chapter Graph
    data object InterviewChapterGraph: NavRoutes("interview_chapter_graph")
    data object InterviewChapterScreen: NavRoutes("interview_chapter")

    // Progress Graph
    data object ProgressGraph: NavRoutes("progress_graph")
    data object ProgressScreen: NavRoutes("progress")
    data object BookResultScreen: NavRoutes("book_result")
}