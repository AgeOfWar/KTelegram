package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Serializable(InlineMessageId.Serializer::class)
data class InlineMessageId internal constructor(
    val messageId: MessageId? = null,
    val inlineId: String? = null
) {
    companion object {
        fun fromMessageId(messageId: MessageId) = InlineMessageId(messageId = messageId)
        fun fromInlineId(inlineId: String) = InlineMessageId(inlineId = inlineId)
    }

    object Serializer : KSerializer<InlineMessageId> {
        override val descriptor = MessageId.serializer().descriptor

        override fun deserialize(decoder: Decoder): InlineMessageId {
            decoder as? JsonDecoder ?: throw UnsupportedOperationException("Only JsonDecoder supported")
            val element = JsonElement.serializer().deserialize(decoder)
            return when {
                element is JsonObject -> fromMessageId(decoder.json.decodeFromJsonElement(MessageId.serializer(), element))
                element is JsonPrimitive && element.isString -> fromInlineId(element.content)
                else -> throw SerializationException("Expected JsonObject or string")
            }
        }

        override fun serialize(encoder: Encoder, value: InlineMessageId) {
            val (messageId, inlineId) = value
            if (messageId != null) encoder.encodeSerializableValue(MessageId.serializer(), messageId)
            if (inlineId != null) encoder.encodeString(inlineId)
        }
    }
}
