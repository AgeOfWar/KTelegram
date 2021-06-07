package com.github.ageofwar.ktelegram

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Required
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

@Serializable(ReplyMarkup.Serializer::class)
sealed class ReplyMarkup {
    object Serializer : JsonContentPolymorphicSerializer<ReplyMarkup>(ReplyMarkup::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out ReplyMarkup> {
            val json = element.jsonObject
            return when {
                "inline_keyboard" in json -> InlineKeyboard.serializer()
                "force_reply" in json -> ForceReply.serializer()
                "keyboard" in json -> Keyboard.serializer()
                "remove_keyboard" in json -> KeyboardRemove.serializer()
                else -> UnknownReplyMarkup.serializer()
            }
        }
    }
}

@Serializable
data class InlineKeyboard(
    @SerialName("inline_keyboard") val keyboard: Array<Array<out Button>>
) : ReplyMarkup() {
    constructor(button: Button) : this(arrayOf(arrayOf(button)))

    companion object {
        fun row(vararg row: Button) = InlineKeyboard(arrayOf(row))
        fun column(vararg column: Button): InlineKeyboard {
            return InlineKeyboard(Array(column.size) { arrayOf(column[it]) })
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InlineKeyboard) return false
        return keyboard.contentDeepEquals(other.keyboard)
    }

    override fun hashCode(): Int {
        return keyboard.contentDeepHashCode()
    }

    override fun toString(): String {
        return keyboard.contentDeepToString()
    }

    @Serializable(Button.Serializer::class)
    sealed class Button {
        abstract val text: String

        @Serializable
        data class Url(
            override val text: String,
            val url: String
        ) : Button()

        @Serializable
        data class CallbackData(
            override val text: String,
            @SerialName("callback_data") val callbackData: String
        ) : Button()

        @Serializable
        data class SwitchInlineQuery(
            override val text: String,
            @SerialName("switch_inline_query") val query: String
        ) : Button()

        @Serializable
        data class SwitchInlineQueryCurrentChat(
            override val text: String,
            @SerialName("switch_inline_query_current_chat") val query: String
        ) : Button()

        @Serializable
        data class Game(
            override val text: String
        ) : Button() {
            @Required
            @SerialName("callback_game")
            private val callbackGame = JsonObject(emptyMap())
        }

        @Serializable
        data class Login(
            override val text: String,
            @SerialName("login_url") val loginUrl: LoginUrl
        ) : Button()

        @Serializable
        data class Pay(
            override val text: String
        ) : Button() {
            @Required
            private val pay = true
        }

        @Serializable
        data class Unknown(override val text: String) : Button()

        object Serializer : JsonContentPolymorphicSerializer<Button>(Button::class) {
            override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Button> {
                val json = element.jsonObject
                return when {
                    "url" in json -> Url.serializer()
                    "callback_data" in json -> CallbackData.serializer()
                    "switch_inline_query" in json -> SwitchInlineQuery.serializer()
                    "switch_inline_query_current_chat" in json -> SwitchInlineQueryCurrentChat.serializer()
                    "callback_game" in json -> Game.serializer()
                    "login_url" in json -> Login.serializer()
                    "pay" in json -> Pay.serializer()
                    else -> Unknown.serializer()
                }
            }
        }
    }
}

@Serializable
data class Keyboard(
    val keyboard: Array<Array<out Button>>,
    @SerialName("resize_keyboard") val resize: Boolean = false,
    @SerialName("one_time_keyboard") val oneTime: Boolean = false,
    @SerialName("selective") val selective: Boolean = false,
) : ReplyMarkup() {
    constructor(button: Button) : this(arrayOf(arrayOf(button)))

    companion object {
        fun row(vararg row: Button) = Keyboard(arrayOf(row))
        fun column(vararg column: Button): Keyboard {
            return Keyboard(Array(column.size) { arrayOf(column[it]) })
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InlineKeyboard) return false
        return keyboard.contentDeepEquals(other.keyboard)
    }

    override fun hashCode(): Int {
        return keyboard.contentDeepHashCode()
    }

    override fun toString(): String {
        return keyboard.contentDeepToString()
    }

    @Serializable(Button.Serializer::class)
    sealed class Button {
        abstract val text: String

        @Serializable
        data class Text(
            override val text: String,
        ) : Button()

        @Serializable
        data class RequestContact(
            override val text: String
        ) : Button() {
            @Required
            @SerialName("request_contact")
            private val requestContact = true
        }

        @Serializable
        data class RequestLocation(
            override val text: String
        ) : Button() {
            @Required
            @SerialName("request_location")
            private val requestLocation = true
        }

        @Serializable
        data class RequestPoll(
            override val text: String,
            @SerialName("request_poll") val pollType: PollType
        ) : Button() {
            @Serializable
            data class PollType(val type: String? = null)
        }

        object Serializer : JsonContentPolymorphicSerializer<Button>(Button::class) {
            override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Button> {
                val json = element.jsonObject
                return when {
                    "request_contact" in json -> RequestContact.serializer()
                    "request_location" in json -> RequestLocation.serializer()
                    "request_poll" in json -> RequestPoll.serializer()
                    else -> Text.serializer()
                }
            }
        }
    }
}

@Serializable
data class KeyboardRemove(val selective: Boolean = false) : ReplyMarkup() {
    @Required
    @SerialName("remove_keyboard")
    private val removeKeyboard = true
}

@Serializable
data class ForceReply(val selective: Boolean = false) : ReplyMarkup() {
    @Required
    @SerialName("force_reply")
    private val forceReply = true
}

@Serializable
object UnknownReplyMarkup : ReplyMarkup()
