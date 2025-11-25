package com.tdd.talktobook.app

import android.app.Application
import android.content.Context
import com.tdd.talktobook.app.di.initKoin

class TalkToBookApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()

        instance = this
    }

    companion object {
        private lateinit var instance: TalkToBookApplication

        fun applicationContext(): Context = instance.applicationContext
    }
}
