package com.tdd.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

private val Context.dataStore by preferencesDataStore(name = "bookshelf_prefs")

class LocalDataStore(context: Context) {
    private val dataStore = context.dataStore

    val userId: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[USER_ID]
        }

    suspend fun saveUserId(userId: String): Boolean {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
            Timber.d("[datastore] user id: $userId")
        }
        return true
    }

    companion object {
        val USER_ID = stringPreferencesKey("user_id")
    }
}