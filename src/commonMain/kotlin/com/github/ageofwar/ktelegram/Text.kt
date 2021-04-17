package com.github.ageofwar.ktelegram

import com.github.ageofwar.ktelegram.text.parseHtml
import com.github.ageofwar.ktelegram.text.parseMarkdown
import com.github.ageofwar.ktelegram.text.toHtml
import com.github.ageofwar.ktelegram.text.toMarkdown
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Text(
    val text: String,
    val entities: List<MessageEntity> = emptyList()
) : CharSequence {
    constructor(text: String, vararg entities: MessageEntity) : this(text, entities.toList())

    override val length get() = text.length

    override operator fun get(index: Int) = text[index]

    override fun subSequence(startIndex: Int, endIndex: Int): Text {
        if (startIndex == 0 && endIndex == text.length) return this
        if (startIndex == endIndex) return EMPTY
        val text = this.text.substring(startIndex, endIndex)
        val entities = mutableListOf<MessageEntity>()
        for (entity in this.entities) {
            val offset = entity.offset
            val length = entity.length
            if (offset >= endIndex) {
                break
            }
            if (offset + length > startIndex) {
                val oldEnd = offset + length
                val newOffset = if (offset > startIndex) offset - startIndex else 0
                val newEnd: Int =
                    if (oldEnd < endIndex) oldEnd - startIndex else endIndex - startIndex
                entities.add(entity.move(newOffset, newEnd - newOffset))
            }
        }
        return Text(text, entities)
    }

    override fun toString() = text

    object MarkdownSerializer : KSerializer<Text> {
        override val descriptor = PrimitiveSerialDescriptor("markdown", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) = Text.parseMarkdown(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Text) {
            encoder.encodeString(value.toMarkdown())
        }
    }

    object HtmlSerializer : KSerializer<Text> {
        override val descriptor = PrimitiveSerialDescriptor("html", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder) = Text.parseHtml(decoder.decodeString())

        override fun serialize(encoder: Encoder, value: Text) {
            encoder.encodeString(value.toHtml())
        }
    }

    companion object {
        val EMPTY = Text("")

        fun bold(text: String): Text {
            return Text(text, MessageEntity.Bold(0, text.length))
        }

        fun italic(text: String): Text {
            return Text(text, MessageEntity.Italic(0, text.length))
        }

        fun underline(text: String): Text {
            return Text(text, MessageEntity.Underline(0, text.length))
        }

        fun strikethrough(text: String): Text {
            return Text(text, MessageEntity.Strikethrough(0, text.length))
        }

        fun code(text: String): Text {
            return Text(text, MessageEntity.Code(0, text.length))
        }

        fun codeBlock(text: String, language: String? = null): Text {
            return Text(text, MessageEntity.Pre(0, text.length, language))
        }

        fun link(link: Link): Text {
            return Text(link.text, MessageEntity.TextLink(0, link.text.length, link.url))
        }

        fun url(url: String): Text {
            return Text(url, MessageEntity.Url(0, url.length))
        }

        fun email(email: String): Text {
            return Text(email, MessageEntity.Email(0, email.length))
        }

        fun hashtag(hashtag: String): Text {
            return Text("#$hashtag", MessageEntity.Hashtag(0, hashtag.length + 1))
        }

        fun mention(mention: Sender): Text {
            return if (mention.username != null) {
                mention(mention.username!!)
            } else {
                textMention(mention)
            }
        }

        fun mention(mention: String): Text {
            return Text("@$mention", MessageEntity.Mention(0, mention.length + 1))
        }

        fun textMention(mention: Mention): Text {
            return textMention(mention.text, mention.sender)
        }

        fun textMention(text: String, mention: Long): Text {
            return Text(text, MessageEntity.TextMention(0, text.length, MessageEntity.TextMention.Sender(mention)))
        }

        fun textMention(mention: Sender): Text {
            return textMention(mention.name, mention.id)
        }

        fun botCommand(botCommand: String): Text {
            return Text("/$botCommand", MessageEntity.BotCommand(0, botCommand.length + 1))
        }

        fun phoneNumber(phoneNumber: String): Text {
            return Text(phoneNumber, MessageEntity.PhoneNumber(0, phoneNumber.length))
        }

        fun cashtag(cashtag: String): Text {
            return Text("$" + cashtag.toUpperCase(), MessageEntity.Cashtag(0, cashtag.length + 1))
        }
    }
}

@Serializable
sealed class MessageEntity {
    abstract val offset: Int
    abstract val length: Int

    val endIndex get() = offset + length

    abstract fun move(offset: Int, length: Int): MessageEntity

    @Serializable
    @SerialName("bold")
    data class Bold(
        override val offset: Int,
        override val length: Int
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("italic")
    data class Italic(
        override val offset: Int,
        override val length: Int
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("underline")
    data class Underline(
        override val offset: Int,
        override val length: Int
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("strikethrough")
    data class Strikethrough(
        override val offset: Int,
        override val length: Int
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("code")
    data class Code(
        override val offset: Int,
        override val length: Int
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("pre")
    data class Pre(
        override val offset: Int,
        override val length: Int,
        val language: String?
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("text_link")
    data class TextLink(
        override val offset: Int,
        override val length: Int,
        val url: String
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("text_mention")
    data class TextMention(
        override val offset: Int,
        override val length: Int,
        @SerialName("user") val sender: Sender
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)

        @Serializable
        data class Sender(val id: Long) {
            val url get() = "tg://user?id=$id"
        }
    }

    @Serializable
    @SerialName("mention")
    data class Mention(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("hashtag")
    data class Hashtag(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("cashtag")
    data class Cashtag(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("bot_command")
    data class BotCommand(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("url")
    data class Url(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("email")
    data class Email(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }

    @Serializable
    @SerialName("phone_number")
    data class PhoneNumber(
        override val offset: Int,
        override val length: Int,
    ) : MessageEntity() {
        override fun move(offset: Int, length: Int) = copy(offset = offset, length = length)
    }
}

@Serializable
data class Link(
    val text: String,
    val url: String
)

@Serializable
data class Mention(
    val text: String,
    val sender: Long
)

fun Text.isCommand() =
    entities.firstOrNull().let { it is MessageEntity.BotCommand && it.offset == 0 }

private inline fun <reified T : MessageEntity, R> Text.getEntities(block: (String, T) -> R): List<R> {
    return entities.filterIsInstance<T>().map {
        block(text.substring(it.offset, it.endIndex), it)
    }
}

fun Text.getBoldText() = getEntities<MessageEntity.Bold, String> { s, _ -> s }
fun Text.getItalicText() = getEntities<MessageEntity.Italic, String> { s, _ -> s }
fun Text.getUnderlineText() = getEntities<MessageEntity.Underline, String> { s, _ -> s }
fun Text.getStrikethroughText() = getEntities<MessageEntity.Strikethrough, String> { s, _ -> s }
fun Text.getCodeText() = getEntities<MessageEntity.Code, String> { s, _ -> s }
fun Text.getCodeBlocks() = getEntities<MessageEntity.Pre, String> { s, _ -> s }
fun Text.getLinks() = getEntities<MessageEntity.TextLink, Link> { s, link -> Link(s, link.url) }
fun Text.getUrls() = getEntities<MessageEntity.Url, String> { s, _ -> s }
fun Text.getEmails() = getEntities<MessageEntity.Email, String> { s, _ -> s }
fun Text.getHashtags() = getEntities<MessageEntity.Hashtag, String> { s, _ -> s.substring(1) }
fun Text.getMentions() = getEntities<MessageEntity.Mention, String> { s, _ -> s.substring(1) }
fun Text.getTextMentions() =
    getEntities<MessageEntity.TextMention, Mention> { s, mention -> Mention(s, mention.sender.id) }

fun Text.getBotCommands() = getEntities<MessageEntity.BotCommand, String> { s, _ -> s.substring(1) }
fun Text.getPhoneNumbers() = getEntities<MessageEntity.PhoneNumber, String> { s, _ -> s }
fun Text.getCashtags() = getEntities<MessageEntity.Cashtag, String> { s, _ -> s.substring(1) }

fun Text.getAllUrls() = getUrls() + getLinks().map { it.url }

inline fun Text.trim(predicate: (Char) -> Boolean = Char::isWhitespace): Text {
    var startIndex = 0
    var endIndex = length - 1
    var startFound = false

    while (startIndex <= endIndex) {
        val index = if (!startFound) startIndex else endIndex
        val match = predicate(this[index])

        if (!startFound) {
            if (!match)
                startFound = true
            else
                startIndex += 1
        } else {
            if (!match)
                break
            else
                endIndex -= 1
        }
    }

    return subSequence(startIndex, endIndex + 1)
}