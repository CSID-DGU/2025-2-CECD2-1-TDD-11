package com.tdd.talktobook.data.entity.response.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmailTokenResponseDto (
    @SerialName("accessToken")
    val accessToken: String = "",
    @SerialName("refreshToken")
    val refreshToken: String = "",
    @SerialName("metadataSuccessed")
    val metaDataSuccess: Boolean = false
)