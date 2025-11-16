package com.tdd.bookshelf.domain.entity.request.default

data class GetQueryDefaultModel(
    val page: Int = 0,
    val size: Int = 10,
)
