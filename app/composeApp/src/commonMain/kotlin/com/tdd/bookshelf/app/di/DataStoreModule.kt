package com.tdd.bookshelf.app.di

import com.tdd.bookshelf.data.dataStore.LocalDataStore
import org.koin.dsl.module

val dataStoreModule =
    module {
        single { LocalDataStore(get()) }
    }
