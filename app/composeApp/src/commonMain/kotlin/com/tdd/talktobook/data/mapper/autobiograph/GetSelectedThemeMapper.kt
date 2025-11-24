package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.SelectedThemeResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object GetSelectedThemeMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<SelectedThemeModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = SelectedThemeResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    SelectedThemeModel(
                        name = data.name,
                        categories = data.categories,
                    )
                } ?: SelectedThemeModel()
            },
        )
    }
}
