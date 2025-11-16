package com.tdd.bookshelf.app.di

import org.koin.core.annotation.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BookShelfKtor

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BookShelfKtorAI

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NoAuthKtor
