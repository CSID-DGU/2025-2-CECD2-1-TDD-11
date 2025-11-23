package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.dataStore.LocalDataStore
import org.koin.dsl.module

val dataStoreModule =
    module {
        single { LocalDataStore(get()) }
    }
