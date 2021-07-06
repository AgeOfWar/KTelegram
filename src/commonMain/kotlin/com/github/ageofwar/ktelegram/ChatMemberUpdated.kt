package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMemberUpdated(
    val chat: Chat,
    @SerialName("from") val sender: Sender,
    val date: Int,
    @SerialName("old_chat_member") val oldChatMember: ChatMember,
    @SerialName("new_chat_member") val newChatMember: ChatMember,
    @SerialName("invite_link") val inviteLink: ChatInviteLink? = null
) {
    val member get() = newChatMember.sender
    val left get() = oldChatMember !is Kicked && oldChatMember !is Left && newChatMember is Left
    val banned get() = oldChatMember !is Kicked && newChatMember is Kicked
    val unbanned get() = oldChatMember is Kicked && newChatMember !is Kicked
    val leftOrBanned get() = oldChatMember !is Left && newChatMember !is Left && (newChatMember is Left || newChatMember is Kicked)
    val join get() = (oldChatMember is Left || oldChatMember is Kicked) && newChatMember !is Left && newChatMember !is Kicked
    val promoted get() = (oldChatMember !is Administrator && oldChatMember !is Creator && newChatMember is Administrator) || (oldChatMember !is Creator && newChatMember is Creator)
    val restricted get() = oldChatMember !is Restricted && newChatMember is Restricted
}