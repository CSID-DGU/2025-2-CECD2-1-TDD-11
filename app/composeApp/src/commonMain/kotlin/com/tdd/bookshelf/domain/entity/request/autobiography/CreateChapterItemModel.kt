package com.tdd.bookshelf.domain.entity.request.autobiography

data class CreateChapterItemModel(
    val number: String = "",
    val name: String = "",
    val description: String = "",
    val subchapters: List<CreateSubChapterItemModel> = emptyList(),
)
