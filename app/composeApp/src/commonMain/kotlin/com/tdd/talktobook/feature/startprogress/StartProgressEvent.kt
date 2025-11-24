package com.tdd.talktobook.feature.startprogress

import com.tdd.talktobook.core.ui.base.Event

sealed class StartProgressEvent : Event {
    data object GoToInterviewPage : StartProgressEvent()
}
