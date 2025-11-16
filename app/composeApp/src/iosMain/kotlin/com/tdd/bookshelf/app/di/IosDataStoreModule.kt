package com.tdd.bookshelf.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tdd.bookshelf.data.dataStore.createDataStore
import com.tdd.bookshelf.data.dataStore.DATA_STORE_FILE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
val iosDataStoreModule =
    module {
        single<DataStore<Preferences>> {
            val documentDirectory: NSURL? =
                NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )
            val path = requireNotNull(documentDirectory).path + "/$DATA_STORE_FILE_NAME"

            createDataStore { path }
        }
    }
