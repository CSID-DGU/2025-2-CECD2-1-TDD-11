package com.tdd.bookshelf.app.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun Application.initKoin(): KoinApplication =
    startKoin {
        androidContext(this@initKoin)
        androidLogger()
        modules(
            androidDataStoreModule,
            dataStoreModule,
            KtorModule.module,
            NoAuthModule.module,
            KtorAIModule.module,
            ServiceModule().module,
            dataSourceModule,
            repositoryModule,
            useCaseModule,
            viewModelModule,
        )
    }
