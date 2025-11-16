package com.tdd.bookshelf.domain.entity.response.publication

data class PublicationProgressUIContentModel(
    val topStartCorner: Int = 0,
    val topEndCorner: Int = 0,
    val bottomStartCorner: Int = 0,
    val bottomEndCorner: Int = 0,
    val titleText: String = "",
    val subTitleText: String = "",
    val stepImg: String = "files/ic_step_first.svg",
)
