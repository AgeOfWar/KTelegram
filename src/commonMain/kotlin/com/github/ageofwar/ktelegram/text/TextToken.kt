package com.github.ageofwar.ktelegram.text

import com.github.ageofwar.ktelegram.MessageEntity
import com.github.ageofwar.ktelegram.Text

sealed class TextToken(val type: TokenType) {
    override fun toString() = this::class.simpleName!!

    data class Text(val text: String) : TextToken(TokenType.TEXT)
    object StartBold : TextToken(TokenType.TAG_START)
    object StartItalic : TextToken(TokenType.TAG_START)
    object StartUnderline : TextToken(TokenType.TAG_START)
    object StartStrikethrough : TextToken(TokenType.TAG_START)
    object StartCode : TextToken(TokenType.TAG_START)
    data class StartPre(val language: String?) : TextToken(TokenType.TAG_START)
    data class StartTextLink(val url: String?) : TextToken(TokenType.TAG_START)
    data class StartTextMention(val sender: MessageEntity.TextMention.Sender?) : TextToken(TokenType.TAG_START)
    object StartMention : TextToken(TokenType.TAG_START)
    object StartHashtag : TextToken(TokenType.TAG_START)
    object StartCashtag : TextToken(TokenType.TAG_START)
    object StartBotCommand : TextToken(TokenType.TAG_START)
    object StartUrl : TextToken(TokenType.TAG_START)
    object StartEmail : TextToken(TokenType.TAG_START)
    object StartPhoneNumber : TextToken(TokenType.TAG_START)
    data class StartCustomEmoji(val customEmojiId: String?) : TextToken(TokenType.TAG_START)
    object StartSpoiler : TextToken(TokenType.TAG_START)
    object EndBold : TextToken(TokenType.TAG_END)
    object EndItalic : TextToken(TokenType.TAG_END)
    object EndUnderline : TextToken(TokenType.TAG_END)
    object EndStrikethrough : TextToken(TokenType.TAG_END)
    object EndCode : TextToken(TokenType.TAG_END)
    data class EndPre(val language: String?) : TextToken(TokenType.TAG_END)
    data class EndTextLink(val url: String?) : TextToken(TokenType.TAG_END)
    data class EndTextMention(val sender: MessageEntity.TextMention.Sender?) : TextToken(TokenType.TAG_END)
    object EndMention : TextToken(TokenType.TAG_END)
    object EndHashtag : TextToken(TokenType.TAG_END)
    object EndCashtag : TextToken(TokenType.TAG_END)
    object EndBotCommand : TextToken(TokenType.TAG_END)
    object EndUrl : TextToken(TokenType.TAG_END)
    object EndEmail : TextToken(TokenType.TAG_END)
    object EndPhoneNumber : TextToken(TokenType.TAG_END)
    object EndSpoiler : TextToken(TokenType.TAG_END)
    data class EndCustomEmoji(val customEmojiId: String?) : TextToken(TokenType.TAG_END)
}

enum class TokenType {
    TEXT,
    TAG_START,
    TAG_END
}

fun Text.toTokens() = mutableListOf<TextToken>().apply {
    var offset = 0
    val endTokens = mutableListOf<MessageEntity>()
    entities.forEach {
        while (endTokens.isNotEmpty() && endTokens.last().endIndex <= it.offset) {
            if (offset != endTokens.last().endIndex) {
                add(TextToken.Text(text.substring(offset, endTokens[0].endIndex)))
            }
            offset = endTokens.last().endIndex
            add(endTokens.removeLast().toEndToken())
        }
        if (offset != it.offset) {
            add(TextToken.Text(text.substring(offset, it.offset)))
            offset = it.offset
        }
        add(it.toStartToken())
        endTokens += it
    }
    while (endTokens.isNotEmpty()) {
        if (offset != endTokens.last().endIndex) {
            add(TextToken.Text(text.substring(offset, endTokens.last().endIndex)))
        }
        offset = endTokens.last().endIndex
        add(endTokens.removeLast().toEndToken())
    }
    if (offset != length) {
        add(TextToken.Text(text.substring(offset)))
    }
}

fun MessageEntity.toStartToken() = when (this) {
    is MessageEntity.Bold -> TextToken.StartBold
    is MessageEntity.Italic -> TextToken.StartItalic
    is MessageEntity.Underline -> TextToken.StartUnderline
    is MessageEntity.Strikethrough -> TextToken.StartStrikethrough
    is MessageEntity.Code -> TextToken.StartCode
    is MessageEntity.Pre -> TextToken.StartPre(language)
    is MessageEntity.TextLink -> TextToken.StartTextLink(url)
    is MessageEntity.TextMention -> TextToken.StartTextMention(sender)
    is MessageEntity.Mention -> TextToken.StartMention
    is MessageEntity.Hashtag -> TextToken.StartHashtag
    is MessageEntity.Cashtag -> TextToken.StartCashtag
    is MessageEntity.BotCommand -> TextToken.StartBotCommand
    is MessageEntity.Url -> TextToken.StartUrl
    is MessageEntity.Email -> TextToken.StartEmail
    is MessageEntity.PhoneNumber -> TextToken.StartPhoneNumber
    is MessageEntity.Spoiler -> TextToken.StartSpoiler
    is MessageEntity.CustomEmoji -> TextToken.StartCustomEmoji(customEmojiId)
}

fun MessageEntity.toEndToken() = when (this) {
    is MessageEntity.Bold -> TextToken.EndBold
    is MessageEntity.Italic -> TextToken.EndItalic
    is MessageEntity.Underline -> TextToken.EndUnderline
    is MessageEntity.Strikethrough -> TextToken.EndStrikethrough
    is MessageEntity.Code -> TextToken.EndCode
    is MessageEntity.Pre -> TextToken.EndPre(language)
    is MessageEntity.TextLink -> TextToken.EndTextLink(url)
    is MessageEntity.TextMention -> TextToken.EndTextMention(sender)
    is MessageEntity.Mention -> TextToken.EndMention
    is MessageEntity.Hashtag -> TextToken.EndHashtag
    is MessageEntity.Cashtag -> TextToken.EndCashtag
    is MessageEntity.BotCommand -> TextToken.EndBotCommand
    is MessageEntity.Url -> TextToken.EndUrl
    is MessageEntity.Email -> TextToken.EndEmail
    is MessageEntity.PhoneNumber -> TextToken.EndPhoneNumber
    is MessageEntity.Spoiler -> TextToken.EndSpoiler
    is MessageEntity.CustomEmoji -> TextToken.EndCustomEmoji(customEmojiId)
}

fun Text.toString(write: (TextToken) -> String) = buildString {
    toTokens().forEach {
        append(write(it))
    }
}

fun List<TextToken>.toText(): Text {
    val textBuilder = StringBuilder()
    val entities = mutableListOf<MessageEntity?>()
    val startTokens = mutableListOf<Pair<TextToken, Int>>()

    fun acceptToken(token: TextToken) {
        startTokens += Pair(token, textBuilder.length)
        entities += null
    }

    fun completeEntity(token: TextToken) {
        val match =
            startTokens.removeLastOrNull() ?: throw TextParseException("Unmatched token $token")
        val (startToken, offset) = match
        entities[entities.lastIndexOf(null)] = when (token) {
            is TextToken.EndBold -> {
                if (startToken !is TextToken.StartBold) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Bold(offset, textBuilder.length - offset)
            }
            is TextToken.EndItalic -> {
                if (startToken !is TextToken.StartItalic) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Italic(offset, textBuilder.length - offset)
            }
            is TextToken.EndUnderline -> {
                if (startToken !is TextToken.StartUnderline) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Underline(offset, textBuilder.length - offset)
            }
            is TextToken.EndStrikethrough -> {
                if (startToken !is TextToken.StartStrikethrough) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Strikethrough(offset, textBuilder.length - offset)
            }
            is TextToken.EndCode -> {
                if (startToken !is TextToken.StartCode) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Code(offset, textBuilder.length - offset)
            }
            is TextToken.EndPre -> {
                if (startToken !is TextToken.StartPre) {
                    throw TextParseException("Unmatched token $token")
                }
                if (startToken.language != null && token.language != null && startToken.language != token.language) {
                    throw TextParseException("Inconsistent language for token $token")
                }
                val language = startToken.language ?: token.language
                MessageEntity.Pre(offset, textBuilder.length - offset, language)
            }
            is TextToken.EndTextLink -> {
                if (startToken !is TextToken.StartTextLink) {
                    throw TextParseException("Unmatched token $token")
                }
                if (startToken.url != null && token.url != null && startToken.url != token.url) {
                    throw TextParseException("Inconsistent url for token $token")
                }
                val url = startToken.url ?: token.url
                ?: throw TextParseException("Url not present in token $token")
                if (url.startsWith("tg://user?id=")) {
                    val userId = url.substring("tg://user?id=".length).toLongOrNull()
                    if (userId != null) {
                        MessageEntity.TextMention(offset, textBuilder.length - offset, MessageEntity.TextMention.Sender(userId))
                    } else {
                        MessageEntity.TextLink(offset, textBuilder.length - offset, url)
                    }
                } else MessageEntity.TextLink(offset, textBuilder.length - offset, url)
            }
            is TextToken.EndTextMention -> {
                if (startToken !is TextToken.StartTextMention) {
                    throw TextParseException("Unmatched token $token")
                }
                if (startToken.sender != null && token.sender != null && startToken.sender != token.sender) {
                    throw TextParseException("Inconsistent sender for token $token")
                }
                val sender = startToken.sender ?: token.sender
                ?: throw TextParseException("Sender not present in token $token")
                MessageEntity.TextMention(offset, textBuilder.length - offset, sender)
            }
            is TextToken.EndMention -> {
                if (startToken !is TextToken.StartMention) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Mention(offset, textBuilder.length - offset)
            }
            is TextToken.EndHashtag -> {
                if (startToken !is TextToken.StartHashtag) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Hashtag(offset, textBuilder.length - offset)
            }
            is TextToken.EndCashtag -> {
                if (startToken !is TextToken.StartCashtag) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Cashtag(offset, textBuilder.length - offset)
            }
            is TextToken.EndBotCommand -> {
                if (startToken !is TextToken.StartBotCommand) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.BotCommand(offset, textBuilder.length - offset)
            }
            is TextToken.EndUrl -> {
                if (startToken !is TextToken.StartUrl) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Url(offset, textBuilder.length - offset)
            }
            is TextToken.EndEmail -> {
                if (startToken !is TextToken.StartEmail) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Email(offset, textBuilder.length - offset)
            }
            is TextToken.EndPhoneNumber -> {
                if (startToken !is TextToken.StartPhoneNumber) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.PhoneNumber(offset, textBuilder.length - offset)
            }
            is TextToken.EndSpoiler -> {
                if (startToken !is TextToken.StartSpoiler) {
                    throw TextParseException("Unmatched token $token")
                }
                MessageEntity.Spoiler(offset, textBuilder.length - offset)
            }
            is TextToken.EndCustomEmoji -> {
                if (startToken !is TextToken.StartCustomEmoji) {
                    throw TextParseException("Unmatched token $token")
                }
                if (startToken.customEmojiId != null && token.customEmojiId != null && startToken.customEmojiId != token.customEmojiId) {
                    throw TextParseException("Inconsistent sender for token $token")
                }
                val customEmojiId = startToken.customEmojiId ?: token.customEmojiId
                ?: throw TextParseException("Custom emoji id not present in token $token")
                MessageEntity.CustomEmoji(offset, textBuilder.length - offset, customEmojiId)
            }
            else -> error("Not an end token!")
        }
    }

    forEach {
        when {
            it is TextToken.Text -> textBuilder.append(it.text)
            it.type == TokenType.TAG_START -> acceptToken(it)
            it.type == TokenType.TAG_END -> completeEntity(it)
        }
    }

    return Text(textBuilder.toString(), entities.map { it ?: throw TextParseException("Unclosed token") })
}

class TextParseException(message: String) : RuntimeException(message)