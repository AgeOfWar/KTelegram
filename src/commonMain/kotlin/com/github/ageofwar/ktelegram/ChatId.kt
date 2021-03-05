package com.github.ageofwar.ktelegram

data class ChatId internal constructor(
    val id: Long? = null,
    val username: String? = null
) {
    companion object {
        fun fromId(id: Long) = ChatId(id = id)
        fun fromUsername(username: String) = ChatId(username = username)
    }
}
