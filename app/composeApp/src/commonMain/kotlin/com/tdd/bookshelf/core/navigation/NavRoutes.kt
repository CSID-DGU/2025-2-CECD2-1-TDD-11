package com.tdd.bookshelf.core.navigation

sealed class NavRoutes(val route: String) {
    // LogIn Graph
    data object LogInGraph : NavRoutes("login_graph")

    data object LogInScreen : NavRoutes("login")

    // SignUp Graph
    data object SignUpGraph : NavRoutes("signup_graph")

    data object SignUpScreen : NavRoutes("signup")

    // Onboarding Graph
    data object OnboardingGraph : NavRoutes("splash_graph")

    data object OnboardingScreen : NavRoutes("splash")

    // Home Graph
    data object HomeGraph : NavRoutes("home_graph")

    data object HomeScreen : NavRoutes("home")

    // Interview Graph
    data object InterviewGraph : NavRoutes("interview_graph")

    data object InterviewScreen : NavRoutes("interview/{interviewId}") {
        fun setRouteModel(interviewId: Int): String = "interview/$interviewId"
    }

    // DetailChapter Graph
    data object DetailChapterGraph : NavRoutes("detail_chapter_graph")

    data object DetailChapterScreen : NavRoutes("detail_chapter/{autobiographyId}") {
        fun setRouteModel(autobiographyId: Int): String = "detail_chapter/$autobiographyId"
    }

    // MyPage Graph
    data object MyPageGraph : NavRoutes("my_page_graph")

    data object MyPageScreen : NavRoutes("my_page")

    // Publication
    data object PublicationGraph : NavRoutes("publication_graph")

    data object PublicationScreen : NavRoutes("publication")
}
