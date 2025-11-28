package com.tdd.talktobook.domain.entity.request.autobiography

data class CreateAutobiographyRequestModel(
    val autobiographyId: Int = 0,
    val name: String = "",
)
