package com.tdd.talktobook.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger.Companion.d
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalDataStore(
    private val dataStore: DataStore<Preferences>,
) {
    val accessToken: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }

    val refreshToken: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }

    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
            d("[dataStore] access token: $token")
        }
    }

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
            d("[dataStore] refresh token: $token")
        }
    }

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
