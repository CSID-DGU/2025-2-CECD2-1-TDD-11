package com.tdd.talktobook.core.navigation

sealed class NavRoutes(val route: String) {
    // LogIn Graph
    data object LogInGraph : NavRoutes("login_graph")

    data object LogInScreen : NavRoutes("login")

    // SignUp Graph
    data object SignUpGraph : NavRoutes("signup_graph")

    data object SignUpScreen : NavRoutes("signup")

    // EmailCheck Graph
    data object EmailCheckGraph : NavRoutes("email_check_graph")

    data object EmailCheckScreen : NavRoutes("email_check/{email}") {
        fun setRouteModel(email: String): String = "email_check/$email"
    }

    // Onboarding Graph
    data object OnboardingGraph : NavRoutes("splash_graph")

    data object OnboardingScreen : NavRoutes("splash")

    // Home Graph
    data object HomeGraph : NavRoutes("home_graph")

    data object HomeScreen : NavRoutes("home")

    // Past Interview Graph
    data object PastInterviewGraph : NavRoutes("past_interview_graph")

    data object PastInterviewScreen : NavRoutes("past_interview/{date}/{interviewId}") {
        fun setRouteModel(
            date: String,
            interviewId: Int,
        ): String = "past_interview/$date/$interviewId"
    }

    // Interview Graph
    data object InterviewGraph : NavRoutes("interview_graph")

    data object InterviewScreen : NavRoutes("interview?question={question}") {
        fun setRouteModel(question: String): String =
            if (question.isBlank()) {
                "interview"
            } else {
                "interview?question=$question"
            }
    }

    // Request Create Graph
    data object AutobiographyRequestGraph : NavRoutes("autobiography_request_graph")

    data object AutobiographyRequestScreen : NavRoutes("autobiography_request/{autobiographyId}") {
        fun setRouteModel(autobiographyId: Int): String = "autobiography_request/$autobiographyId"
    }

    // StartProgress Graph
    data object StartProgressGraph : NavRoutes("start_progress_graph")

    data object StartProgressScreen : NavRoutes("start_progress")

    // DetailChapter Graph
    data object DetailChapterGraph : NavRoutes("detail_chapter_graph")

    data object DetailChapterScreen : NavRoutes("detail_chapter/{autobiographyId}") {
        fun setRouteModel(autobiographyId: Int): String = "detail_chapter/$autobiographyId"
    }

    // MyPage Graph
    data object SettingPageGraph : NavRoutes("setting_page_graph")

    data object SettingPageScreen : NavRoutes("setting_page")

    // Publication
    data object PublicationGraph : NavRoutes("publication_graph")

    data object PublicationScreen : NavRoutes("publication")
}
