package com.tdd.talktobook.domain.entity.response.autobiography

data class CurrentProgressAutobiographyModel (
    val autobiographyId: Int = 0,
    val isProgress: Boolean = false,
    val message: String = ""
)