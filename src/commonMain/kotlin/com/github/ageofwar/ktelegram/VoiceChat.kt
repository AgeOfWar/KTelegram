package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Deprecated("Use VideoChatStarted", ReplaceWith("VideoChatStarted"))
typealias VoiceChatStarted = VideoChatStarted

@Serializable
object VideoChatStarted

@Deprecated("Use VideoChatEnded", ReplaceWith("VideoChatEnded"))
typealias VoiceChatEnded = VideoChatEnded

@Serializable
data class VideoChatEnded(
    val duration: Int
)

@Deprecated("Use VideoChatParticipantsInvited", ReplaceWith("VideoChatParticipantsInvited"))
typealias VoiceChatParticipantsInvited = VideoChatParticipantsInvited

@Serializable
data class VideoChatParticipantsInvited(
    val users: List<User>
)

@Deprecated("Use VideoChatScheduled", ReplaceWith("VideoChatScheduled"))
typealias VoiceChatScheduled = VideoChatScheduled

@Serializable
data class VideoChatScheduled(
    @SerialName("start_date") val startDate: Int
)