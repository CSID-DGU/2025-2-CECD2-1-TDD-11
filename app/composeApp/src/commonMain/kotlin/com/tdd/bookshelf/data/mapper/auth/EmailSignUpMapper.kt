package com.tdd.bookshelf.data.mapper.auth

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.auth.EmailLogInResponseDto
import com.tdd.bookshelf.domain.entity.response.auth.AccessTokenModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object EmailSignUpMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<AccessTokenModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = EmailLogInResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    AccessTokenModel(
                        accessToken = data.accessToken,
                    )
                } ?: AccessTokenModel()
            },
        )
    }
}
