package com.github.ageofwar.ktelegram

import kotlinx.serialization.Serializable

@Serializable
object VoiceChatStarted

@Serializable
data class VoiceChatEnded(
    val duration: Int
)

@Serializable
data class VoiceChatParticipantsInvited(
    val users: List<User>
)
