package com.tdd.interviewchapter

import com.tdd.ui.base.Event

sealed class InterviewChapterEvent: Event {
    data object ShowChapterBottomSheet: InterviewChapterEvent()
}