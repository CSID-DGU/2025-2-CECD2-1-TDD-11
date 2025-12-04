package com.tdd.talktobook.app.di

import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

fun initKoin() {
    startKoin {
        modules(
            tokenModule,
            iosDataStoreModule,
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
}
