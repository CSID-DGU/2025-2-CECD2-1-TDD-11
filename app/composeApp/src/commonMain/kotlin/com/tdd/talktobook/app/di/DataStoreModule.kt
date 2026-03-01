package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.dataStore.FireBaseDataStore
import com.tdd.talktobook.data.dataStore.LocalDataStore
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import org.koin.dsl.module

val dataStoreModule =
    module {
        single { LocalDataStore(get()) }

        single<FirebaseFirestore> { Firebase.firestore }
        single { FireBaseDataStore(get()) }
    }
