package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
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

@Serializable
data class VoiceChatScheduled(
    @SerialName("start_date") val startDate: Int
)