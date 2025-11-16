package com.tdd.bookshelf.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tdd.bookshelf.data.dataStore.DATA_STORE_FILE_NAME
import com.tdd.bookshelf.data.dataStore.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDataStoreModule =
    module {
        single<DataStore<Preferences>> {
            val context: Context = androidContext()
            val path = context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath

            createDataStore { path }
        }
    }
