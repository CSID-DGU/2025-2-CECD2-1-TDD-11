package com.tdd.talktobook.domain.entity.request.autobiography

data class CreateAutobiographyChaptersRequestModel(
    val chapters: List<CreateChapterItemModel> = emptyList(),
)
