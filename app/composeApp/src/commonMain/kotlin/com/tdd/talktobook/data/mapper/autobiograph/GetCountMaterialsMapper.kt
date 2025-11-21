package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.CountMaterialsResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsResponseModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetCountMaterialsMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<CountMaterialsResponseModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = CountMaterialsResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    CountMaterialsResponseModel(
                        autobiographyId = data.autobiographyId,
                        popularMaterials = data.popularMaterials.map { material ->
                            CountMaterialsItemModel(
                                id = material.id,
                                order = material.order,
                                rank = material.rank,
                                name = material.name,
                                imageUrl = material.imageUrl,
                                count = material.count
                            )
                        },
                        currentPage = data.currentPage,
                        totalPages = data.totalPages,
                        totalElements = data.totalElements,
                        isLast = data.isLast
                    )
                } ?: CountMaterialsResponseModel()
            }
        )
    }
}