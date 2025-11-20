package com.tdd.talktobook.data.mapper.auth

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.request.auth.EmailLogInRequestDto
import com.tdd.talktobook.data.entity.response.auth.EmailTokenResponseDto
import com.tdd.talktobook.domain.entity.request.auth.EmailLogInRequestModel
import com.tdd.talktobook.domain.entity.response.auth.TokenModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object EmailLogInMapper : BaseMapper() {
    fun EmailLogInRequestModel.toDto() =
        EmailLogInRequestDto(
            email = email,
            password = password,
            deviceToken = deviceToken,
        )

    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<TokenModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = EmailTokenResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    TokenModel(
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken,
                        metadataSuccess = data.metaDataSuccess
                    )
                } ?: TokenModel()
            },
        )
    }
}
