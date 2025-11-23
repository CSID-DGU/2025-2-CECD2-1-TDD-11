package com.tdd.talktobook.feature.interview

import com.tdd.talktobook.core.ui.base.Event

sealed class InterviewEvent: Event {
    data object ShowStartAutobiographyDialog: InterviewEvent()
}