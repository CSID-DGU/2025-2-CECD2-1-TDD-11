package com.tdd.talktobook.app

import android.app.Application
import com.tdd.talktobook.app.di.initKoin

class BookShelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
