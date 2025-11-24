package com.tdd.talktobook.data.mapper.auth

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.auth.ReissueTokenResponseDto
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object ReissueMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<TokenModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = ReissueTokenResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    TokenModel(
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken,
                        metadataSuccess = true,
                    )
                } ?: TokenModel()
            },
        )
    }
}
