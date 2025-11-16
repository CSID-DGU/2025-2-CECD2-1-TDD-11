package com.tdd.bookshelf.domain.entity.request.autobiography

data class CreateAutobiographyChaptersRequestModel(
    val chapters: List<CreateChapterItemModel> = emptyList(),
)
