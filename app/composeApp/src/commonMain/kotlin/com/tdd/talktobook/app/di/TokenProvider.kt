package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.dataStore.LocalDataStore
import kotlinx.coroutines.flow.firstOrNull

class TokenProvider(
    private val localDataStore: LocalDataStore,
) {
    suspend fun getAccessToken(): String? {
        return localDataStore.accessToken.firstOrNull()
    }
}
