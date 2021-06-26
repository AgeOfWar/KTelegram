package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BotCommand(
    val command: String,
    val description: String
) {
    init {
        require(command.length in 1..32) { "Bot command length should be 1..32" }
        require(description.length in 3..256) { "Bot command description length should be 3..256" }
    }
}

@Serializable
sealed class BotCommandScope {
    @Serializable
    @SerialName("default")
    object Default : BotCommandScope()

    @Serializable
    @SerialName("all_private_chats")
    object AllPrivateChats : BotCommandScope()

    @Serializable
    @SerialName("all_group_chats")
    object AllGroupChats : BotCommandScope()

    @Serializable
    @SerialName("all_chat_administrators")
    object AllChatAdministrators : BotCommandScope()

    @Serializable
    @SerialName("chat")
    data class Chat(@SerialName("chat_id") val chatId: ChatId) : BotCommandScope()

    @Serializable
    @SerialName("chat_administrators")
    data class ChatAdministrators(@SerialName("chat_id") val chatId: ChatId) : BotCommandScope()

    @Serializable
    @SerialName("chat_member")
    data class ChatMember(
        @SerialName("chat_id") val chatId: ChatId,
        @SerialName("user_id") val userId: Long
    ) : BotCommandScope()
}
