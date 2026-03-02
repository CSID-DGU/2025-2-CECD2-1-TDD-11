package com.tdd.talktobook.data.mapper.member

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.member.MemberProfileResponseDto
import com.tdd.talktobook.domain.entity.response.member.MemberProfileModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object MemberProfileMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<MemberProfileModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = MemberProfileResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    MemberProfileModel(
                        memberId = data.memberId,
                        nickname = data.nickname,
                        profileImageUrl = data.profileImageUrl,
                    )
                } ?: MemberProfileModel()
            },
        )
    }
}
