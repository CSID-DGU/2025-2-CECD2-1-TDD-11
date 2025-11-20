package com.tdd.talktobook.feature.home

import com.tdd.talktobook.core.ui.base.Event

sealed class HomeEvent : Event {
    data object GoToDetailChapterPage : HomeEvent()
}
