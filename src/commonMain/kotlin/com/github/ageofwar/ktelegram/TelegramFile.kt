package com.github.ageofwar.ktelegram

interface TelegramFile : Id<String> {
    val fileId: String
    val fileSize: Long
}
