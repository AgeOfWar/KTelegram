package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.long

@Serializable(ChatId.Serializer::class)
data class ChatId internal constructor(
    val id: Long? = null,
    val username: String? = null
) {
    companion object {
        fun fromId(id: Long) = ChatId(id = id)
        fun fromUsername(username: String) = ChatId(username = username)
    }

    object Serializer : KSerializer<ChatId> {
        override val descriptor = PrimitiveSerialDescriptor("ChatId", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): ChatId {
            val element = JsonPrimitive.serializer().deserialize(decoder)
            return when {
                element.isString -> fromUsername(element.content)
                else -> fromId(element.long)
            }
        }

        override fun serialize(encoder: Encoder, value: ChatId) {
            val (id, username) = value
            if (id != null) encoder.encodeLong(id)
            if (username != null) encoder.encodeString(username)
        }
    }
}
