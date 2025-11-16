package com.tdd.bookshelf.data.entity.response.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(val message: String? = null)
