package com.tdd.domain.entity.response.progress

data class ProgressBookInfoModel (
    val bookTitle: String = "",
    val bookTitles: List<String> = emptyList(),
    val startDate: String = "",
    val endDate: String = "",
    val page: Int = 0,
    val price: String = ""
)