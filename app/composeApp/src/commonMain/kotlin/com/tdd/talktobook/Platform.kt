package com.tdd.talktobook

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
