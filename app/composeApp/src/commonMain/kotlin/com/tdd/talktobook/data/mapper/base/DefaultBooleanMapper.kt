package com.tdd.talktobook.data.mapper.base

import com.tdd.talktobook.data.base.BaseMapper
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object DefaultBooleanMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<Boolean>> {
        return baseMapper<Nothing, Boolean>(
            apiCall = { apiCall() },
            successDeserializer = null,
            responseToModel = { true },
        )
    }
}
