package com.tdd.talktobook.feature.interview

import com.tdd.talktobook.core.ui.base.Event

sealed class InterviewEvent : Event {
    data object ShowStartAutobiographyDialog : InterviewEvent()

    data object ShowCreateAutobiographyDialog : InterviewEvent()

    data object GoBackToLogIn : InterviewEvent()

    data object ShowNetworkErrorToast : InterviewEvent()

    data object GoBackToHome : InterviewEvent()

    data object ShowPublicationSuccessToast : InterviewEvent()
}
