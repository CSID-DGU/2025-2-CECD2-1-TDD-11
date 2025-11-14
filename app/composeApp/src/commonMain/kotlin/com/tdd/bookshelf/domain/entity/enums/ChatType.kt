package com.tdd.bookshelf.domain.entity.enums

enum class ChatType(
    val content: String,
) {
    HUMAN("HUMAN"),
    BOT("BOT"),
    ;

    companion object {
        fun getType(type: String): ChatType =
            entries.firstOrNull { it.content == type } ?: BOT
    }
}
