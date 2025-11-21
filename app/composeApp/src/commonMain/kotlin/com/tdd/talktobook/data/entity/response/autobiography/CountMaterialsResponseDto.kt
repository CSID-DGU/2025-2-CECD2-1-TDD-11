package com.tdd.talktobook.data.entity.response.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountMaterialsResponseDto (
    @SerialName("popularMaterials")
    val popularMaterials: List<PopularMaterial> = emptyList(),
    @SerialName("currentPage")
    val currentPage: Int = 0,
    @SerialName("totalPages")
    val totalPages: Int = 0,
    @SerialName("totalElements")
    val totalElements: Int = 0,
    @SerialName("isLast")
    val isLast: Boolean = false
){
    @Serializable
    data class PopularMaterial(
        @SerialName("id")
        val id: Int = 0,
        @SerialName("order")
        val order: Int = 0,
        @SerialName("rank")
        val rank: Int = 0,
        @SerialName("name")
        val name: String = "",
        @SerialName("imageUrl")
        val imageUrl: String? = null,
        @SerialName("count")
        val count: Int = 0
    )
}