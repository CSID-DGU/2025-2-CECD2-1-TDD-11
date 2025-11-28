package com.tdd.talktobook.app.di

import org.koin.dsl.module

val tokenModule =
    module {
        single { TokenProvider(get()) }
    }
