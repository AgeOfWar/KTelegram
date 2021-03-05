package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable(Sender.Serializer::class)
sealed class Sender : Id<Long>, Username, Name {
    object Serializer : JsonContentPolymorphicSerializer<Sender>(Sender::class) {
        override fun selectDeserializer(element: JsonElement): KSerializer<out Sender> {
            return when (element.jsonObject["is_bot"]?.jsonPrimitive?.boolean) {
                true -> Bot.serializer()
                false -> User.serializer()
                null -> Anonymous.serializer()
            }
        }
    }
}

@Serializable(User.Serializer::class)
data class User(
    override val id: Long,
    override val username: String?,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null,
    @SerialName("language_code") val languageCode: String?
) : Sender() {
    object Serializer : KSerializer<User> {
        override val descriptor = buildClassSerialDescriptor("User") {
            element<Boolean>("is_bot")
            element<Long>("id")
            element<String?>("username")
            element<String>("first_name")
            element<String?>("last_name")
            element<String?>("language_code")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var isBot = true
            var id: Long? = null
            var username: String? = null
            var firstName: String? = null
            var lastName: String? = null
            var languageCode: String? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> isBot = decodeBooleanElement(descriptor, 0)
                    1 -> id = decodeLongElement(descriptor, 1)
                    2 -> username = decodeStringElement(descriptor, 2)
                    3 -> firstName = decodeStringElement(descriptor, 3)
                    4 -> lastName = decodeStringElement(descriptor, 4)
                    5 -> languageCode = decodeStringElement(descriptor, 5)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(!isBot) { "Not a User!" }
            requireNotNull(id)
            requireNotNull(username)
            requireNotNull(firstName)
            User(id, username, firstName, lastName, languageCode)
        }

        override fun serialize(encoder: Encoder, value: User) {
            encoder.encodeStructure(descriptor) {
                val (id, username, firstName, lastName, languageCode) = value
                encodeBooleanElement(descriptor, 0, false)
                encodeLongElement(descriptor, 1, id)
                if (username != null) encodeStringElement(descriptor, 2, username)
                encodeStringElement(descriptor, 3, firstName)
                if (lastName != null) encodeStringElement(descriptor, 4, lastName)
                if (languageCode != null) encodeStringElement(descriptor, 5, languageCode)
            }
        }
    }
}

@Serializable(Bot.Serializer::class)
data class Bot(
    override val id: Long,
    override val username: String,
    @SerialName("first_name") override val firstName: String,
    @SerialName("last_name") override val lastName: String? = null
) : Sender() {
    object Serializer : KSerializer<Bot> {
        override val descriptor = buildClassSerialDescriptor("Bot") {
            element<Boolean>("is_bot")
            element<Long>("id")
            element<String>("username")
            element<String>("first_name")
            element<String?>("last_name")
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var isBot = false
            var id: Long? = null
            var username: String? = null
            var firstName: String? = null
            var lastName: String? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> isBot = decodeBooleanElement(descriptor, 0)
                    1 -> id = decodeLongElement(descriptor, 1)
                    2 -> username = decodeStringElement(descriptor, 2)
                    3 -> firstName = decodeStringElement(descriptor, 3)
                    4 -> lastName = decodeStringElement(descriptor, 4)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            check(isBot) { "Not a Bot!" }
            requireNotNull(id)
            requireNotNull(username)
            requireNotNull(firstName)
            Bot(id, username, firstName, lastName)
        }

        override fun serialize(encoder: Encoder, value: Bot) {
            encoder.encodeStructure(descriptor) {
                val (id, username, firstName, lastName) = value
                encodeBooleanElement(descriptor, 0, true)
                encodeLongElement(descriptor, 1, id)
                encodeStringElement(descriptor, 2, username)
                encodeStringElement(descriptor, 3, firstName)
                if (lastName != null) encodeStringElement(descriptor, 4, lastName)
            }
        }
    }
}

@Serializable(Anonymous.Serializer::class)
data class Anonymous(val chat: Chat) : Sender() {
    override val id get() = chat.id
    override val firstName
        get() = when (chat) {
            is PrivateChat -> chat.firstName
            is Channel -> chat.title
            is Group -> chat.title
            is Supergroup -> chat.title
        }
    override val lastName get() = if (chat is Name) chat.lastName else null
    override val username get() = chat.username

    object Serializer : KSerializer<Anonymous> {
        override val descriptor = Chat.serializer().descriptor

        override fun deserialize(decoder: Decoder) =
            Anonymous(decoder.decodeSerializableValue(Chat.serializer()))

        override fun serialize(encoder: Encoder, value: Anonymous) =
            encoder.encodeSerializableValue(Chat.serializer(), value.chat)
    }
}

val Sender.url get() = "tg://user?id=$id"
