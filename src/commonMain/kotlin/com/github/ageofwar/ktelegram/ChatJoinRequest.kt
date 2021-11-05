package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatJoinRequest(
    val chat: Chat,
    @SerialName("from") val sender: User,
    val date: Long,
    val bio: String? = null,
    @SerialName("invite_link") val inviteLink: ChatInviteLink? = null
)