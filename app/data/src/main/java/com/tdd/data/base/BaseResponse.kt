package com.tdd.data.base

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerializedName("success")
    val success: Boolean? = false,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("data")
    val data: T? = null
)