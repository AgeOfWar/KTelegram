package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatInviteLink(
    @SerialName("invite_link") val inviteLink: String,
    val creator: Sender,
    @SerialName("is_primary") val isPrimary: Boolean,
    @SerialName("is_revoked") val isRevoked: Boolean,
    @SerialName("expire_date") val expireDate: Int? = null,
    @SerialName("member_limit") val numberLimit: Int? = null
)