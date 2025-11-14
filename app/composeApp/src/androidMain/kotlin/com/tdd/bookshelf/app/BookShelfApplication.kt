package com.tdd.bookshelf.app

import android.app.Application
import com.tdd.bookshelf.app.di.initKoin

class BookShelfApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
