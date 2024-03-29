package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class DetailedChat : Id<Long> {
    abstract val chatPhoto: ChatPhoto?
    abstract val inviteLink: String?
    abstract val pinnedMessage: Message?
    abstract val permissions: ChatPermissions
    abstract val stickerSetName: String?
    abstract val canSetStickerSet: Boolean
    abstract val activeUsernames: List<String>
}

@Serializable
@SerialName("private")
data class DetailedPrivateChat(
    override val id: Long,
    override val username: String? = null,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    @SerialName("chat_photo") override val chatPhoto: ChatPhoto? = null,
    val bio: String? = null,
    @SerialName("has_private_forwards") val hasPrivateForwards: Boolean = false,
    @SerialName("has_restricted_voice_and_video_messages") val hasRestrictedVoiceAndVideoMessages: Boolean = false,
    @SerialName("active_usernames") override val activeUsernames: List<String> = emptyList(),
    @SerialName("emoji_status_custom_emoji_id") val emojiStatusCustomEmojiId: String? = null
) : DetailedChat(), Username, Name {
    override val inviteLink: Nothing? get() = null
    override val pinnedMessage: Nothing? get() = null
    override val permissions
        get() = ChatPermissions(
            canSendMessage = true,
            canSendMediaMessages = true,
            canSendPolls = false,
            canSendOtherMessages = true,
            canAddWebPagePreviews = true,
            canChangeInfo = false,
            canInviteUsers = false,
            canPinMessages = false
        )
    override val canSetStickerSet get() = false
    override val stickerSetName: Nothing? get() = null
}

@Serializable
@SerialName("group")
data class DetailedGroup(
    override val id: Long,
    override val title: String,
    @SerialName("chat_photo") override val chatPhoto: ChatPhoto? = null,
    override val description: String? = null,
    @SerialName("invite_link") override val inviteLink: String? = null,
    @SerialName("pinned_message") override val pinnedMessage: Message? = null,
    override val permissions: ChatPermissions,
    @SerialName("sticker_set_name") override val stickerSetName: String? = null,
    @SerialName("can_set_sticker_set") override val canSetStickerSet: Boolean,
    @SerialName("active_usernames") override val activeUsernames: List<String> = emptyList()
) : DetailedChat(), Title, Description

@Serializable
@SerialName("supergroup")
data class DetailedSupergroup(
    override val id: Long,
    override val username: String? = null,
    override val title: String,
    @SerialName("is_forum") val isForum: Boolean = false,
    @SerialName("chat_photo") override val chatPhoto: ChatPhoto? = null,
    override val description: String? = null,
    @SerialName("invite_link") override val inviteLink: String? = null,
    @SerialName("pinned_message") override val pinnedMessage: Message? = null,
    override val permissions: ChatPermissions,
    @SerialName("slow_mode_delay") val slowModeDelay: Int? = null,
    @SerialName("sticker_set_name") override val stickerSetName: String? = null,
    @SerialName("can_set_sticker_set") override val canSetStickerSet: Boolean,
    @SerialName("linked_chat_id") val linkedChatId: Long? = null,
    val location: ChatLocation? = null,
    @SerialName("join_to_send_messages") val joinToSendMessages: Boolean = false,
    @SerialName("join_by_request") val joinByRequest: Boolean = false,
    @SerialName("active_usernames") override val activeUsernames: List<String> = emptyList()
) : DetailedChat(), Username, Title, Description

@Serializable
@SerialName("channel")
data class DetailedChannel(
    override val id: Long,
    override val username: String? = null,
    override val title: String,
    @SerialName("chat_photo") override val chatPhoto: ChatPhoto? = null,
    override val description: String? = null,
    @SerialName("invite_link") override val inviteLink: String? = null,
    @SerialName("pinned_message") override val pinnedMessage: Message? = null,
    @SerialName("linked_chat_id") val linkedChatId: Long? = null,
    @SerialName("active_usernames") override val activeUsernames: List<String> = emptyList()
) : DetailedChat(), Username, Title, Description {
    override val stickerSetName: Nothing? get() = null
    override val canSetStickerSet get() = false
    override val permissions
        get() = ChatPermissions(
            canSendMessage = false,
            canSendMediaMessages = false,
            canSendPolls = false,
            canSendOtherMessages = false,
            canAddWebPagePreviews = false,
            canChangeInfo = false,
            canInviteUsers = false,
            canPinMessages = false
        )
}

fun DetailedChat.toChat() = when (this) {
    is DetailedPrivateChat -> toPrivateChat()
    is DetailedGroup -> toGroup()
    is DetailedSupergroup -> toSupergroup()
    is DetailedChannel -> toChannel()
}

fun DetailedPrivateChat.toPrivateChat() = PrivateChat(id, username, firstName, lastName)
fun DetailedGroup.toGroup() = Group(id, title)
fun DetailedSupergroup.toSupergroup() = Supergroup(id, username, title, isForum)
fun DetailedChannel.toChannel() = Channel(id, username, title)
