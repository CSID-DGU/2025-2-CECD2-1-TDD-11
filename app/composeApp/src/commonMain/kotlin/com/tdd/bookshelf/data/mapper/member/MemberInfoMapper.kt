package com.tdd.bookshelf.data.mapper.member

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.member.MemberInfoResponseDto
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel
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
                        name = data.name,
                        bornedAt = data.bornedAt,
                        gender = data.gender,
                        hasChildren = data.hasChildren,
                        occupation = data.occupation,
                        educationLevel = data.educationLevel,
                        maritalStatus = data.maritalStatus,
                    )
                } ?: MemberInfoModel()
            },
        )
    }
}
