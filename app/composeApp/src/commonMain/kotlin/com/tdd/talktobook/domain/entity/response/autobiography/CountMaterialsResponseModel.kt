package com.tdd.talktobook.domain.entity.response.autobiography

data class CountMaterialsResponseModel(
    val autobiographyId: Int = 0,
    val popularMaterials: List<CountMaterialsItemModel> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val isLast: Boolean = false,
)