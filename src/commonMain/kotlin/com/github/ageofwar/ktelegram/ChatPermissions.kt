package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPermissions(
    @SerialName("can_send_messages") val canSendMessage: Boolean = false,
    @SerialName("can_send_media_messages") val canSendMediaMessages: Boolean = false,
    @SerialName("can_send_polls") val canSendPolls: Boolean = false,
    @SerialName("can_send_other_messages") val canSendOtherMessages: Boolean = false,
    @SerialName("can_add_web_page_previews") val canAddWebPagePreviews: Boolean = false,
    @SerialName("can_change_info") val canChangeInfo: Boolean = false,
    @SerialName("can_invite_users") val canInviteUsers: Boolean = false,
    @SerialName("can_pin_messages") val canPinMessages: Boolean = false,
)

@Deprecated("Use ChatAdministratorRights instead", ReplaceWith("ChatAdministratorRights"))
typealias AdminPermissions = ChatAdministratorRights

@Serializable
data class ChatAdministratorRights(
    @SerialName("is_anonymous") val isAnonymous: Boolean = false,
    @SerialName("can_manage_chat") val canManageChat: Boolean = false,
    @SerialName("can_change_info") val canChangeInfo: Boolean = false,
    @SerialName("can_post_messages") val canPostMessages: Boolean = false,
    @SerialName("can_edit_messages") val canEditMessages: Boolean = false,
    @SerialName("can_delete_messages") val canDeleteMessages: Boolean = false,
    @SerialName("can_invite_users") val canInviteUsers: Boolean = false,
    @SerialName("can_restrict_members") val canRestrictMembers: Boolean = false,
    @SerialName("can_pin_messages") val canPinMessages: Boolean = false,
    @SerialName("can_promote_members") val canPromoteMembers: Boolean = false,
    @SerialName("can_manage_video_chats") val canManageVoiceChats: Boolean = false
)
