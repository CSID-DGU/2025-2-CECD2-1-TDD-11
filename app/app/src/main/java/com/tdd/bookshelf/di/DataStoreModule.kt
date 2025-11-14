package com.tdd.bookshelf.di

import android.content.Context
import com.tdd.data.dataStore.LocalDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providesDataStore(@ApplicationContext context: Context): LocalDataStore {
        return LocalDataStore(context)
    }
}