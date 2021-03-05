package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*

@Serializable(with = DetailedBot.Serializer::class)
data class DetailedBot(
    override val id: Long,
    override val username: String,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    @SerialName("can_join_groups") val canJoinGroups: Boolean,
    @SerialName("can_read_all_group_messages") val canReadAllGroupMessages: Boolean,
    @SerialName("supports_inline_queries") val supportsInlineQueries: Boolean
) : Id<Long>, Username, Name {
    object Serializer : KSerializer<DetailedBot> {
        override val descriptor = buildClassSerialDescriptor("DetailedBot") {
            element<Boolean>("is_bot")
            element<Long>("id")
            element<String>("username")
            element<String>("first_name")
            element<String?>("last_name")
            element<Boolean>("can_join_groups")
            element<Boolean>("can_read_all_group_messages")
            element<Boolean>("supports_inline_queries")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var id: Long? = null
            var username: String? = null
            var firstName: String? = null
            var lastName: String? = null
            var canJoinGroups: Boolean? = null
            var canReadAllGroupMessages: Boolean? = null
            var supportsInlineQueries: Boolean? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(decodeBooleanElement(descriptor, 0)) { "Not a Bot!" }
                    1 -> id = decodeLongElement(descriptor, 1)
                    2 -> username = decodeStringElement(descriptor, 2)
                    3 -> firstName = decodeStringElement(descriptor, 3)
                    4 -> lastName = decodeStringElement(descriptor, 4)
                    5 -> canJoinGroups = decodeBooleanElement(descriptor, 5)
                    6 -> canReadAllGroupMessages = decodeBooleanElement(descriptor, 6)
                    7 -> supportsInlineQueries = decodeBooleanElement(descriptor, 7)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(id)
            requireNotNull(username)
            requireNotNull(firstName)
            requireNotNull(canJoinGroups)
            requireNotNull(canReadAllGroupMessages)
            requireNotNull(supportsInlineQueries)
            DetailedBot(
                id,
                username,
                firstName,
                lastName,
                canJoinGroups,
                canReadAllGroupMessages,
                supportsInlineQueries
            )
        }

        override fun serialize(encoder: Encoder, value: DetailedBot) =
            encoder.encodeStructure(descriptor) {
                val (id, username, firstName, lastName, canJoinGroups, canReadAllGroupMessages, supportsInlineQueries) = value
                encodeBooleanElement(descriptor, 0, false)
                encodeLongElement(descriptor, 1, id)
                encodeStringElement(descriptor, 2, username)
                encodeStringElement(descriptor, 3, firstName)
                if (lastName != null) encodeStringElement(descriptor, 4, lastName)
                encodeBooleanElement(descriptor, 5, canJoinGroups)
                encodeBooleanElement(descriptor, 6, canReadAllGroupMessages)
                encodeBooleanElement(descriptor, 7, supportsInlineQueries)
            }
    }
}

fun DetailedBot.toBot() = Bot(id, username, firstName, lastName)
