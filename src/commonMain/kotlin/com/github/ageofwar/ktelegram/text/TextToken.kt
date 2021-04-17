package com.github.ageofwar.ktelegram.text

import com.github.ageofwar.ktelegram.MessageEntity
import com.github.ageofwar.ktelegram.Text

sealed class TextToken(private val start: Boolean) {
    fun isStart() = start
    fun isEnd() = !start

    override fun toString() = this::class.simpleName!!

    data class Text(val text: String) : TextToken(false)
    object StartBold : TextToken(true)
    object StartItalic : TextToken(true)
    object StartUnderline : TextToken(true)
    object StartStrikethrough : TextToken(true)
    object StartCode : TextToken(true)
    data class StartPre(val language: String?) : TextToken(true)
    data class StartTextLink(val url: String?) : TextToken(true)
    data class StartTextMention(val sender: MessageEntity.TextMention.Sender?) : TextToken(true)
    object StartMention : TextToken(true)
    object StartHashtag : TextToken(true)
    object StartCashtag : TextToken(true)
    object StartBotCommand : TextToken(true)
    object StartUrl : TextToken(true)
    object StartEmail : TextToken(true)
    object StartPhoneNumber : TextToken(true)
    object EndBold : TextToken(false)
    object EndItalic : TextToken(false)
    object EndUnderline : TextToken(false)
    object EndStrikethrough : TextToken(false)
    object EndCode : TextToken(false)
    data class EndPre(val language: String?) : TextToken(false)
    data class EndTextLink(val url: String?) : TextToken(false)
    data class EndTextMention(val sender: MessageEntity.TextMention.Sender?) : TextToken(false)
    object EndMention : TextToken(false)
    object EndHashtag : TextToken(false)
    object EndCashtag : TextToken(false)
    object EndBotCommand : TextToken(false)
    object EndUrl : TextToken(false)
    object EndEmail : TextToken(false)
    object EndPhoneNumber : TextToken(false)
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

    fun acceptNestableToken(token: TextToken) {
        val nestable = startTokens.all { (token, _) ->
            token is TextToken.StartBold ||
                    token is TextToken.StartItalic ||
                    token is TextToken.StartUnderline ||
                    token is TextToken.StartStrikethrough
        }
        if (!nestable) throw TextParseException("Cannot nest $token because one or more tokens are not nestable")
        startTokens += Pair(token, textBuilder.length)
        entities += null
    }

    fun acceptToken(token: TextToken) {
        if (startTokens.isNotEmpty()) {
            throw TextParseException("Token $token is not nestable")
        }
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
            else -> error("Not an end token!")
        }
    }

    forEach {
        when (it) {
            is TextToken.Text -> textBuilder.append(it.text)
            is TextToken.StartBold -> acceptNestableToken(it)
            is TextToken.StartItalic -> acceptNestableToken(it)
            is TextToken.StartUnderline -> acceptNestableToken(it)
            is TextToken.StartStrikethrough -> acceptNestableToken(it)
            is TextToken.StartCode -> acceptToken(it)
            is TextToken.StartPre -> acceptToken(it)
            is TextToken.StartTextLink -> acceptToken(it)
            is TextToken.StartTextMention -> acceptToken(it)
            is TextToken.StartMention -> acceptToken(it)
            is TextToken.StartHashtag -> acceptToken(it)
            is TextToken.StartCashtag -> acceptToken(it)
            is TextToken.StartBotCommand -> acceptToken(it)
            is TextToken.StartUrl -> acceptToken(it)
            is TextToken.StartEmail -> acceptToken(it)
            is TextToken.StartPhoneNumber -> acceptToken(it)
            is TextToken.EndBold -> completeEntity(it)
            is TextToken.EndItalic -> completeEntity(it)
            is TextToken.EndUnderline -> completeEntity(it)
            is TextToken.EndStrikethrough -> completeEntity(it)
            is TextToken.EndCode -> completeEntity(it)
            is TextToken.EndPre -> completeEntity(it)
            is TextToken.EndTextLink -> completeEntity(it)
            is TextToken.EndTextMention -> completeEntity(it)
            is TextToken.EndMention -> completeEntity(it)
            is TextToken.EndHashtag -> completeEntity(it)
            is TextToken.EndCashtag -> completeEntity(it)
            is TextToken.EndBotCommand -> completeEntity(it)
            is TextToken.EndUrl -> completeEntity(it)
            is TextToken.EndEmail -> completeEntity(it)
            is TextToken.EndPhoneNumber -> completeEntity(it)
        }
    }

    return Text(textBuilder.toString(), entities.map { it!! })
}

class TextParseException(message: String) : RuntimeException(message)