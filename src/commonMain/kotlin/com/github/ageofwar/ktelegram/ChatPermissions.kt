package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatPermissions(
    @SerialName("can_send_messages") val canSendMessage: Boolean,
    @SerialName("can_send_media_messages") val canSendMediaMessages: Boolean,
    @SerialName("can_send_polls") val canSendPolls: Boolean,
    @SerialName("can_send_other_messages") val canSendOtherMessages: Boolean,
    @SerialName("can_add_web_page_previews") val canAddWebPagePreviews: Boolean,
    @SerialName("can_change_info") val canChangeInfo: Boolean,
    @SerialName("can_invite_users") val canInviteUsers: Boolean,
    @SerialName("can_pin_messages") val canPinMessages: Boolean = false,
)

@Serializable
data class AdminPermissions(
    @SerialName("is_anonymous") val isAnonymous: Boolean,
    @SerialName("can_change_info") val canChangeInfo: Boolean,
    @SerialName("can_post_messages") val canPostMessages: Boolean,
    @SerialName("can_edit_messages") val canEditMessages: Boolean,
    @SerialName("can_delete_messages") val canDeleteMessages: Boolean,
    @SerialName("can_invite_users") val canInviteUsers: Boolean,
    @SerialName("can_restrict_members") val canRestrictMembers: Boolean,
    @SerialName("can_pin_messages") val canPinMessages: Boolean = false,
    @SerialName("can_promote_members") val canPromoteMembers: Boolean,
)
