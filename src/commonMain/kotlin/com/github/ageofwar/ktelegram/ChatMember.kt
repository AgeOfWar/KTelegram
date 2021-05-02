package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = ChatMember.Serializer::class)
sealed class ChatMember {
    abstract val sender: Sender

    object Serializer : JsonContentPolymorphicSerializer<ChatMember>(ChatMember::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ChatMember> {
            return when (element.jsonObject["status"]?.jsonPrimitive?.content) {
                "creator" -> Creator.serializer()
                "administrator" -> Administrator.serializer()
                "member" -> Member.serializer()
                "restricted" -> Restricted.serializer()
                "left" -> Left.serializer()
                "kicked" -> Kicked.serializer()
                else -> Member.serializer() // fallback serializer
            }
        }
    }
}

@Serializable
data class Creator(
    @SerialName("user") override val sender: Sender,
    @SerialName("custom_title") val customTitle: String? = null,
    @SerialName("is_anonymous") val isAnonymous: Boolean = false
) : ChatMember() {
    @Required
    private val status = "creator"
}

@Serializable(with = Administrator.Serializer::class)
data class Administrator(
    @SerialName("user") override val sender: Sender,
    @SerialName("custom_title") val customTitle: String? = null,
    @SerialName("can_be_edited") val canBeEdited: Boolean,
    val permissions: AdminPermissions
) : ChatMember() {
    @Required
    private val status = "administrator"

    object Serializer : KSerializer<Administrator> {
        override val descriptor = buildClassSerialDescriptor("administrator") {
            element<String>("status")
            element<Sender>("user")
            element<String?>("custom_title")
            element<Boolean?>("can_be_edited")
            element<Boolean?>("is_anonymous")
            element<Boolean?>("can_change_info")
            element<Boolean?>("can_post_messages")
            element<Boolean?>("can_edit_messages")
            element<Boolean?>("can_delete_messages")
            element<Boolean?>("can_invite_users")
            element<Boolean?>("can_restrict_members")
            element<Boolean?>("can_pin_messages")
            element<Boolean?>("can_promote_members")
            element<Boolean?>("can_manage_voice_chats")
            element<Boolean?>("can_manage_chat")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var sender: Sender? = null
            var customTitle: String? = null
            var canBeEdited: Boolean? = null
            var isAnonymous = false
            var canChangeInfo = false
            var canPostMessages = false
            var canEditMessages = false
            var canDeleteMessages = false
            var canInviteUsers = false
            var canRestrictMembers = false
            var canPinMessages = false
            var canPromoteMembers = false
            var canManageVoiceChats = false
            var canManageChat = false
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(decodeStringElement(descriptor, 0) == "administrator") {
                        "Not an Administrator!"
                    }
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> customTitle = decodeStringElement(descriptor, 2)
                    3 -> canBeEdited = decodeBooleanElement(descriptor, 3)
                    4 -> isAnonymous = decodeBooleanElement(descriptor, 4)
                    5 -> canChangeInfo = decodeBooleanElement(descriptor, 5)
                    6 -> canPostMessages = decodeBooleanElement(descriptor, 6)
                    7 -> canEditMessages = decodeBooleanElement(descriptor, 7)
                    8 -> canDeleteMessages = decodeBooleanElement(descriptor, 8)
                    9 -> canInviteUsers = decodeBooleanElement(descriptor, 9)
                    10 -> canRestrictMembers = decodeBooleanElement(descriptor, 10)
                    11 -> canPinMessages = decodeBooleanElement(descriptor, 11)
                    12 -> canPromoteMembers = decodeBooleanElement(descriptor, 12)
                    13 -> canManageVoiceChats = decodeBooleanElement(descriptor, 13)
                    14 -> canManageChat = decodeBooleanElement(descriptor, 14)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(sender)
            requireNotNull(canBeEdited)
            Administrator(
                sender,
                customTitle,
                canBeEdited,
                AdminPermissions(
                    isAnonymous,
                    canManageChat,
                    canChangeInfo,
                    canPostMessages,
                    canEditMessages,
                    canDeleteMessages,
                    canInviteUsers,
                    canRestrictMembers,
                    canPinMessages,
                    canPromoteMembers,
                    canManageVoiceChats,
                )
            )
        }

        override fun serialize(encoder: Encoder, value: Administrator) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, "administrator")
                encodeSerializableElement(descriptor, 1, Sender.serializer(), value.sender)
                if (value.customTitle != null) encodeStringElement(descriptor, 2, value.customTitle)
                encodeBooleanElement(descriptor, 3, value.canBeEdited)
                encodeBooleanElement(descriptor, 4, value.permissions.isAnonymous)
                encodeBooleanElement(descriptor, 5, value.permissions.canChangeInfo)
                encodeBooleanElement(descriptor, 6, value.permissions.canPostMessages)
                encodeBooleanElement(descriptor, 7, value.permissions.canEditMessages)
                encodeBooleanElement(descriptor, 8, value.permissions.canDeleteMessages)
                encodeBooleanElement(descriptor, 9, value.permissions.canInviteUsers)
                encodeBooleanElement(descriptor, 10, value.permissions.canRestrictMembers)
                encodeBooleanElement(descriptor, 11, value.permissions.canPinMessages)
                encodeBooleanElement(descriptor, 12, value.permissions.canPromoteMembers)
            }
        }
    }
}

@Serializable
data class Member(
    @SerialName("user") override val sender: Sender
) : ChatMember() {
    @Required
    private val status = "member"
}

@Serializable(with = Restricted.Serializer::class)
data class Restricted(
    @SerialName("user") override val sender: Sender,
    @SerialName("is_member") val isMember: Boolean,
    val permissions: ChatPermissions,
    @SerialName("until_date") val untilDate: Int? = null
) : ChatMember() {
    @Required
    private val status = "restricted"

    object Serializer : KSerializer<Restricted> {
        override val descriptor = buildClassSerialDescriptor("restricted") {
            element<String>("status")
            element<Sender>("user")
            element<Boolean>("is_member")
            element<Boolean?>("can_send_messages")
            element<Boolean?>("can_send_media_messages")
            element<Boolean?>("can_send_polls")
            element<Boolean?>("can_send_other_messages")
            element<Boolean?>("can_add_web_page_previews")
            element<Boolean?>("can_change_info")
            element<Boolean?>("can_invite_users",)
            element<Boolean?>("can_pin_messages")
            element<Int?>("until_date")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var sender: Sender? = null
            var isMember: Boolean? = null
            var canSendMessage = false
            var canSendMediaMessages = false
            var canSendPolls = false
            var canSendOtherMessages = false
            var canAddWebPagePreviews = false
            var canChangeInfo = false
            var canInviteUsers = false
            var canPinMessages = false
            var untilDate: Int? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(decodeStringElement(descriptor, 0) == "restricted") {
                        "Not a Restricted!"
                    }
                    1 -> sender =
                        decodeSerializableElement(descriptor, 1, Sender.serializer(), sender)
                    2 -> isMember = decodeBooleanElement(descriptor, 2)
                    3 -> canSendMessage = decodeBooleanElement(descriptor, 3)
                    4 -> canSendMediaMessages = decodeBooleanElement(descriptor, 4)
                    5 -> canSendPolls = decodeBooleanElement(descriptor, 5)
                    6 -> canSendOtherMessages = decodeBooleanElement(descriptor, 6)
                    7 -> canAddWebPagePreviews = decodeBooleanElement(descriptor, 7)
                    8 -> canChangeInfo = decodeBooleanElement(descriptor, 8)
                    9 -> canInviteUsers = decodeBooleanElement(descriptor, 9)
                    10 -> canPinMessages = decodeBooleanElement(descriptor, 10)
                    11 -> untilDate = decodeIntElement(descriptor, 11)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(sender)
            requireNotNull(isMember)
            Restricted(
                sender,
                isMember,
                ChatPermissions(
                    canSendMessage,
                    canSendMediaMessages,
                    canSendPolls,
                    canSendOtherMessages,
                    canAddWebPagePreviews,
                    canChangeInfo,
                    canInviteUsers,
                    canPinMessages
                ),
                untilDate
            )
        }

        override fun serialize(encoder: Encoder, value: Restricted) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, "restricted")
                encodeSerializableElement(descriptor, 1, Sender.serializer(), value.sender)
                encodeBooleanElement(descriptor, 2, value.isMember)
                encodeBooleanElement(descriptor, 3, value.permissions.canSendMessage)
                encodeBooleanElement(descriptor, 4, value.permissions.canSendMediaMessages)
                encodeBooleanElement(descriptor, 5, value.permissions.canSendPolls)
                encodeBooleanElement(descriptor, 6, value.permissions.canSendOtherMessages)
                encodeBooleanElement(descriptor, 7, value.permissions.canAddWebPagePreviews)
                encodeBooleanElement(descriptor, 8, value.permissions.canChangeInfo)
                encodeBooleanElement(descriptor, 9, value.permissions.canInviteUsers)
                encodeBooleanElement(descriptor, 10, value.permissions.canPinMessages)
                if (value.untilDate != null) encodeIntElement(descriptor, 11, value.untilDate)
            }
        }
    }
}

@Serializable
data class Left(
    @SerialName("user") override val sender: Sender
) : ChatMember() {
    @Required
    private val status = "left"
}

@Serializable
data class Kicked(
    @SerialName("user") override val sender: Sender,
    @SerialName("until_date") val untilDate: Int? = null
) : ChatMember() {
    @Required
    private val status = "kicked"
}