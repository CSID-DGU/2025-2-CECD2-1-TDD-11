package com.tdd.talktobook.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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

    val currentAutobiographyStatus: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[CURRENT_AUTOBIOGRAPHY_STATUS]
        }
    
    val currentAutobiographyId: Flow<Int?> =
        dataStore.data.map { preferences ->
            preferences[CURRENT_AUTOBIOGRAPHY_ID]
        }

    val lastQuestion: Flow<String?> =
        dataStore.data.map { preferences ->
            preferences[LAST_QUESTION]
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

    suspend fun saveCurrentAutobiographyStatus(currentStatus: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_AUTOBIOGRAPHY_STATUS] = currentStatus
            d("[dataStore] current status: $currentStatus")
        }
    }

    suspend fun saveCurrentAutobiographyId(currentId: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_AUTOBIOGRAPHY_ID] = currentId
            d("[dataStore] current id: $currentId")
        }
    }

    suspend fun saveLastQuestion(chat: String) {
        dataStore.edit { preferences ->
            preferences[LAST_QUESTION] = chat
            d("[dataStore] last question: $chat")
        }
    }

    companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val CURRENT_AUTOBIOGRAPHY_STATUS = stringPreferencesKey("current_autobiography_status")
        val CURRENT_AUTOBIOGRAPHY_ID = intPreferencesKey("current_autobiography_id")
        val LAST_QUESTION = stringPreferencesKey("last_question")
    }
}
