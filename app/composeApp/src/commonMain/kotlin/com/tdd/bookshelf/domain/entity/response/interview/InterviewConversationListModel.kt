package com.tdd.bookshelf.domain.entity.response.interview

data class InterviewConversationListModel(
    val results: List<InterviewChatItem> = emptyList(),
    val currentPage: Int = 0,
    val totalElements: Int = 0,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
)
