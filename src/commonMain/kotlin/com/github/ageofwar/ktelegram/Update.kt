package com.github.ageofwar.ktelegram

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = Update.Serializer::class)
sealed class Update : Id<Long> {
    object Serializer : JsonContentPolymorphicSerializer<Update>(Update::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Update> {
            val json = element.jsonObject
            return when {
                "message" in json -> MessageUpdate.serializer()
                "edited_message" in json -> EditedMessageUpdate.serializer()
                "channel_post" in json -> ChannelPostUpdate.serializer()
                "edited_channel_post" in json -> EditedChannelPostUpdate.serializer()
                "inline_query" in json -> InlineQueryUpdate.serializer()
                "chosen_inline_result" in json -> ChosenInlineResultUpdate.serializer()
                "callback_query" in json -> CallbackQueryUpdate.serializer()
                "poll" in json -> PollUpdate.serializer()
                "poll_answer" in json -> PollAnswerUpdate.serializer()
                else -> UnknownUpdate.serializer()
            }
        }
    }
}

@Serializable
data class MessageUpdate(
    @SerialName("update_id") override val id: Long,
    val message: Message
) : Update()

@Serializable
data class EditedMessageUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("edited_message") val message: Message
) : Update() {
    val lastEditDate get() = message.lastEditDate!!
}

@Serializable
data class ChannelPostUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("channel_post") val message: Message
) : Update()

@Serializable
data class EditedChannelPostUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("edited_channel_post") val message: Message
) : Update() {
    val lastEditDate get() = message.lastEditDate!!
}

@Serializable
data class InlineQueryUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("inline_query") val inlineQuery: InlineQuery
) : Update()

@Serializable
data class ChosenInlineResultUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("chosen_inline_result") val chosenInlineResult: ChosenInlineResult
) : Update()

@Serializable
data class CallbackQueryUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("callback_query") val callbackQuery: CallbackQuery
) : Update()

@Serializable
data class PollUpdate(
    @SerialName("update_id") override val id: Long,
    val poll: Poll
) : Update()

@Serializable
data class PollAnswerUpdate(
    @SerialName("update_id") override val id: Long,
    @SerialName("poll_answer") val pollAnswer: PollAnswer
) : Update()

@Serializable
class UnknownUpdate(@SerialName("update_id") override val id: Long) : Update()
