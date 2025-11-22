package com.tdd.talktobook.data.mapper.member

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.member.MemberInfoResponseDto
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object MemberInfoMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<MemberInfoModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = MemberInfoResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    MemberInfoModel(
                        gender = data.gender,
                        occupation = data.occupation ?: "",
                        ageGroup = data.ageGroup ?: ""
                    )
                } ?: MemberInfoModel()
            },
        )
    }
}
