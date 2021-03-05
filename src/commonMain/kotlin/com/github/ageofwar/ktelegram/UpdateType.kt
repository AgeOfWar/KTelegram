package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

object UpdateTypeSerializer : KSerializer<KClass<out Update>> {
    override val descriptor = PrimitiveSerialDescriptor("update_type", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder) = when (val type = decoder.decodeString()) {
        "message" -> MessageUpdate::class
        "edited_message" -> EditedMessageUpdate::class
        "channel_post" -> ChannelPostUpdate::class
        "edited_channel_post" -> EditedChannelPostUpdate::class
        "inline_query" -> InlineQueryUpdate::class
        "chosen_inline_result" -> ChosenInlineResultUpdate::class
        "callback_query" -> CallbackQueryUpdate::class
        "poll" -> PollUpdate::class
        "poll_answer" -> PollAnswerUpdate::class
        else -> throw SerializationException("Invalid type \"$type\"")
    }

    override fun serialize(encoder: Encoder, value: KClass<out Update>) {
        encoder.encodeString(value.toJsonString())
    }
}

object UpdateTypeSetSerializer : KSerializer<Set<KClass<out Update>>> {
    val serializer = SetSerializer(UpdateTypeSerializer)

    override val descriptor = serializer.descriptor

    override fun deserialize(decoder: Decoder) = decoder.decodeSerializableValue(serializer)

    override fun serialize(encoder: Encoder, value: Set<KClass<out Update>>) {
        encoder.encodeSerializableValue(serializer, value)
    }
}

internal fun <T : Update> KClass<out T>.toJsonString() = when (this) {
    MessageUpdate::class -> "message"
    EditedMessageUpdate::class -> "edited_message"
    ChannelPostUpdate::class -> "channel_post"
    EditedChannelPostUpdate::class -> "edited_channel_post"
    InlineQueryUpdate::class -> "inline_query"
    ChosenInlineResultUpdate::class -> "chosen_inline_result"
    CallbackQueryUpdate::class -> "callback_query"
    PollUpdate::class -> "poll"
    PollAnswerUpdate::class -> "poll_answer"
    else -> throw SerializationException("Invalid type ${Update::class}")
}
