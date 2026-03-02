package com.tdd.talktobook.data.mapper.member

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.member.MemberInfoResponseDto
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object MemberInfoMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<MemberInfoResponseModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = MemberInfoResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    MemberInfoResponseModel(
                        gender = data.gender,
                        occupation = data.occupation ?: "",
                        ageGroup = data.ageGroup ?: "",
                        successed = data.success,
                    )
                } ?: MemberInfoResponseModel()
            },
        )
    }
}
