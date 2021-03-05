package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
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
                else -> throw SerializationException("Unknown or missing status for ChatMember")
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
            element("status", String.serializer().descriptor)
            element("user", Sender.serializer().descriptor)
            element("custom_title", String.serializer().descriptor, isOptional = true)
            element("can_be_edited", Boolean.serializer().descriptor)
            element("is_anonymous", Boolean.serializer().descriptor)
            element("can_change_info", Boolean.serializer().descriptor)
            element("can_post_messages", Boolean.serializer().descriptor)
            element("can_edit_messages", Boolean.serializer().descriptor)
            element("can_delete_messages", Boolean.serializer().descriptor)
            element("can_invite_users", Boolean.serializer().descriptor)
            element("can_restrict_members", Boolean.serializer().descriptor)
            element("can_pin_messages", Boolean.serializer().descriptor, isOptional = true)
            element("can_promote_members", Boolean.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var sender: Sender? = null
            var customTitle: String? = null
            var canBeEdited: Boolean? = null
            var isAnonymous: Boolean? = null
            var canChangeInfo: Boolean? = null
            var canPostMessages: Boolean? = null
            var canEditMessages: Boolean? = null
            var canDeleteMessages: Boolean? = null
            var canInviteUsers: Boolean? = null
            var canRestrictMembers: Boolean? = null
            var canPinMessages = false
            var canPromoteMembers: Boolean? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(
                        decodeStringElement(
                            descriptor,
                            0
                        ) == "administrator"
                    ) { "Not an Administrator!" }
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
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            Administrator(
                sender!!,
                customTitle,
                canBeEdited!!,
                AdminPermissions(
                    isAnonymous!!,
                    canChangeInfo!!,
                    canPostMessages!!,
                    canEditMessages!!,
                    canDeleteMessages!!,
                    canInviteUsers!!,
                    canRestrictMembers!!,
                    canPinMessages,
                    canPromoteMembers!!
                )
            )
        }

        override fun serialize(encoder: Encoder, value: Administrator) =
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
            element("status", String.serializer().descriptor)
            element("user", Sender.serializer().descriptor)
            element("is_member", Boolean.serializer().descriptor)
            element("can_send_messages", Boolean.serializer().descriptor)
            element("can_send_media_messages", Boolean.serializer().descriptor)
            element("can_send_polls", Boolean.serializer().descriptor)
            element("can_send_other_messages", Boolean.serializer().descriptor)
            element("can_add_web_page_previews", Boolean.serializer().descriptor)
            element("can_change_info", Boolean.serializer().descriptor)
            element("can_invite_users", Boolean.serializer().descriptor)
            element("can_pin_messages", Boolean.serializer().descriptor, isOptional = true)
            element("until_date", Int.serializer().descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var sender: Sender? = null
            var isMember: Boolean? = null
            var canSendMessage: Boolean? = null
            var canSendMediaMessages: Boolean? = null
            var canSendPolls: Boolean? = null
            var canSendOtherMessages: Boolean? = null
            var canAddWebPagePreviews: Boolean? = null
            var canChangeInfo: Boolean? = null
            var canInviteUsers: Boolean? = null
            var canPinMessages = false
            var untilDate: Int? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(
                        decodeStringElement(
                            descriptor,
                            0
                        ) == "restricted"
                    ) { "Not a Restricted!" }
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
            Restricted(
                sender!!,
                isMember!!,
                ChatPermissions(
                    canSendMessage!!,
                    canSendMediaMessages!!,
                    canSendPolls!!,
                    canSendOtherMessages!!,
                    canAddWebPagePreviews!!,
                    canChangeInfo!!,
                    canInviteUsers!!,
                    canPinMessages
                ),
                untilDate
            )
        }

        override fun serialize(encoder: Encoder, value: Restricted) =
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