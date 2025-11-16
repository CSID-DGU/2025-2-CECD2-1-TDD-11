package com.tdd.bookshelf.data.mapper.autobiograph

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.autobiography.AutobiographiesDetailResponseDto
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object AutobiographiesDetailMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<AutobiographiesDetailModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = AutobiographiesDetailResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    AutobiographiesDetailModel(
                        autobiographyId = data.autobiographyId,
                        interviewId = data.interviewId,
                        title = data.title,
                        content = data.content,
                        coverImageUrl = data.coverImageUrl,
                        createdAt = data.createdAt,
                        updatedAt = data.updatedAt,
                    )
                } ?: AutobiographiesDetailModel()
            },
        )
    }
}
