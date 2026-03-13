package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.dataStore.LocalDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SessionManager(
    private val localDataStore: LocalDataStore,
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val logoutMutex = Mutex()
    private var isLoggedOut = false

    suspend fun logout() {
        logoutMutex.withLock {
            if (isLoggedOut) return
            isLoggedOut = true

            localDataStore.clearAll()
            _authState.emit(AuthState.Unauthenticated)
        }
    }

    suspend fun setAuthenticated() {
        logoutMutex.withLock {
            isLoggedOut = false
            _authState.emit(AuthState.Authenticated)
        }
    }
}
