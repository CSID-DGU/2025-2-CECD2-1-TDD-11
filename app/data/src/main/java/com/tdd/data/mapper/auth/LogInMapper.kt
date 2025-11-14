package com.tdd.data.mapper.auth

import com.tdd.data.base.BaseMapper
import com.tdd.data.base.BaseResponse
import com.tdd.data.entity.request.auth.LogInRequestDto
import com.tdd.data.entity.response.auth.LogInResponseDto
import com.tdd.domain.entity.request.auth.AuthRequestModel
import com.tdd.domain.entity.response.auth.AuthResponseModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

object LogInMapper : BaseMapper() {

    fun AuthRequestModel.toDto() = LogInRequestDto(
        deviceId = deviceId
    )

    fun responseToModel(apiCall: suspend () -> Response<LogInResponseDto>): Flow<Result<AuthResponseModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            responseToModel = { response ->
                response?.let { data ->
                    AuthResponseModel(
                        userId = data.userId,
                        profileCompleted = data.profileCompleted
                    )
                } ?: AuthResponseModel()
            }
        )
    }
}