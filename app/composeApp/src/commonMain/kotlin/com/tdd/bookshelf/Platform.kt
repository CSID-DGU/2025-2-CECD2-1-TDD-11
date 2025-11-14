package com.tdd.bookshelf

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
