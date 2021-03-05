package com.github.ageofwar.ktelegram

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(CallbackQuery.Serializer::class)
sealed class CallbackQuery : Id<String> {
    abstract val sender: User
    abstract val chatInstance: String
    abstract val message: Message?
    abstract val inlineMessageId: String?

    object Serializer : JsonContentPolymorphicSerializer<CallbackQuery>(CallbackQuery::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out CallbackQuery> {
            val json = element.jsonObject
            return when {
                "data" in json -> CallbackData.serializer()
                "game_short_name" in json -> CallbackGame.serializer()
                else -> UnknownCallbackQuery.serializer()
            }
        }
    }
}

@Serializable
data class CallbackData(
    override val id: String,
    @SerialName("from") override val sender: User,
    override val message: Message? = null,
    @SerialName("inline_message_id") override val inlineMessageId: String? = null,
    @SerialName("chat_instance") override val chatInstance: String,
    val data: String
) : CallbackQuery() {
    init {
        check(message != null || inlineMessageId != null) { "One of message and inlineMessageId should be not null" }
    }
}

@Serializable
data class CallbackGame(
    override val id: String,
    @SerialName("from") override val sender: User,
    override val message: Message? = null,
    @SerialName("inline_message_id") override val inlineMessageId: String? = null,
    @SerialName("chat_instance") override val chatInstance: String,
    @SerialName("game_short_name") val gameShortName: String
) : CallbackQuery() {
    init {
        check(message != null || inlineMessageId != null) { "One of message and inlineMessageId should be not null" }
    }
}

@Serializable
data class UnknownCallbackQuery(
    override val id: String,
    @SerialName("from") override val sender: User,
    override val message: Message? = null,
    @SerialName("inline_message_id") override val inlineMessageId: String? = null,
    @SerialName("chat_instance") override val chatInstance: String
) : CallbackQuery() {
    init {
        check(message != null || inlineMessageId != null) { "One of message and inlineMessageId should be not null" }
    }
}

val CallbackQuery.messageId get() = InlineMessageId(message?.messageId, inlineMessageId)
