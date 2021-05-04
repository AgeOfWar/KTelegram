package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class Chat : Id<Long>

@Serializable
@SerialName("private")
data class PrivateChat(
    override val id: Long,
    override val username: String? = null,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null
) : Chat(), Username, Name

@Serializable
@SerialName("group")
data class Group(
    override val id: Long,
    override val title: String
) : Chat(), Title

@Serializable
@SerialName("supergroup")
data class Supergroup(
    override val id: Long,
    override val username: String? = null,
    override val title: String
) : Chat(), Username, Title

@Serializable
@SerialName("channel")
data class Channel(
    override val id: Long,
    override val username: String? = null,
    override val title: String
) : Chat(), Username, Title

val Chat.username get() = if (this is Username) username else null
val Chat.title get() = if (this is Title) title else null
val Chat.chatId get() = ChatId(id, username)
