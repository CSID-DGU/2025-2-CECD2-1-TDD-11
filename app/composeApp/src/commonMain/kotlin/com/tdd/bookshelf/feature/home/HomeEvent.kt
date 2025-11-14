package com.tdd.bookshelf.feature.home

import com.tdd.bookshelf.core.ui.base.Event

sealed class HomeEvent : Event {
    data object GoToDetailChapterPage : HomeEvent()
}
