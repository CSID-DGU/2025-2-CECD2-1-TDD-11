package com.tdd.firebase.di

import com.tdd.firebase.fcmtoken.FcmTokenProvider
import com.tdd.firebase.fcmtoken.FcmTokenProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FcmTokenModule {
    @Provides
    @Singleton
    fun provideFcmTokenProviderImpl(): FcmTokenProvider = FcmTokenProviderImpl()
}