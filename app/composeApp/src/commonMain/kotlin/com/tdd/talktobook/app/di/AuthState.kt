package com.tdd.talktobook.app.di

sealed interface AuthState {
    data object Authenticated : AuthState
    data object Unauthenticated : AuthState
}