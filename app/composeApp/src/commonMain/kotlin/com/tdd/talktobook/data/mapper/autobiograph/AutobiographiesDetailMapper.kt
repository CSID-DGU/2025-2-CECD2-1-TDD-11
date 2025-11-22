package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.AutobiographiesDetailResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel
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
