package com.github.ageofwar.ktelegram.text

import com.github.ageofwar.ktelegram.Text
import com.github.ageofwar.ktelegram.text.TextToken.*

fun Text.toHtml() = toString { it.toHtml() }

fun String.toHtml(): String = replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")

fun TextToken.toHtml() = when (this) {
    is TextToken.Text -> text.toHtml()
    is StartBold -> "<b>"
    is StartItalic -> "<i>"
    is StartUnderline -> "<u>"
    is StartStrikethrough -> "<s>"
    is StartCode -> "<code>"
    is StartPre -> if (language != null) "<pre><code class=\"$language\">" else "<pre>"
    is StartTextLink -> "<a href=\"${url!!}\">"
    is StartTextMention -> "<a href=\"${sender!!.url}\">"
    is EndBold -> "</b>"
    is EndItalic -> "</i>"
    is EndUnderline -> "</u>"
    is EndStrikethrough -> "</s>"
    is EndCode -> "</code>"
    is EndPre -> if (language != null) "</code></pre>" else "</pre>"
    is EndTextLink -> "</a>"
    is EndTextMention -> "</a>"
    else -> ""
}


fun String.parseHtml() = replace("&lt;", "<")
    .replace("&gt;", ">")
    .replace("&quot;", "\"")
    .replace("&amp;", "&")

fun Text.Companion.parseHtml(string: String): Text {
    val tokens = mutableListOf<TextToken>()
    var textBuilder = StringBuilder()
    var i = 0

    while (i < string.length) {
        when (val c = string[i]) {
            '<' -> {
                if (textBuilder.isNotEmpty()) {
                    tokens += TextToken.Text(textBuilder.toString())
                    textBuilder = StringBuilder()
                }

                val isEnd = string.getOrNull(i + 1) == '/'
                var closeIndex = string.indexOf('>', i + 1)
                if (closeIndex < 0) throw TextParseException("Unterminated tag at index $i")
                val tag = string.substring(i + if (isEnd) 2 else 1, closeIndex).trim().toLowerCase()
                when {
                    tag.isEmpty() -> throw TextParseException("Empty tag at index $i")
                    tag.equals("b", ignoreCase = true) || tag.equals(
                        "strong",
                        ignoreCase = true
                    ) -> tokens += if (isEnd) EndBold else StartBold
                    tag.equals("i", ignoreCase = true) || tag.equals(
                        "em",
                        ignoreCase = true
                    ) -> tokens += if (isEnd) EndItalic else StartItalic
                    tag.equals("u", ignoreCase = true) || tag.equals(
                        "ins",
                        ignoreCase = true
                    ) -> tokens += if (isEnd) EndUnderline else StartUnderline
                    tag.equals("s", ignoreCase = true) || tag.equals(
                        "strike",
                        ignoreCase = true
                    ) || tag.equals(
                        "del",
                        ignoreCase = true
                    ) -> tokens += if (isEnd) EndStrikethrough else StartStrikethrough
                    isEnd && tag.equals("a", ignoreCase = true) -> tokens += EndTextLink(null)
                    tag.startsWith("a", ignoreCase = true) -> {
                        val attributes = tag.substring(1).trim()
                        val href = attributes.removePrefix("href=").removeSurrounding("\"")
                            .removeSurrounding("'")
                        tokens += StartTextLink(href.parseHtml())
                    }
                    tag.equals("code", ignoreCase = true) -> {
                        val lastToken = tokens.lastOrNull { it is StartPre } as StartPre?
                        if (isEnd && lastToken?.language != null) {
                            if (string.getOrNull(closeIndex + 1) != '<') throw TextParseException("Missing end code tag at index ${closeIndex + 1}")
                            val preCloseIndex = string.indexOf('>', closeIndex + 2)
                            if (preCloseIndex < 0) throw TextParseException("Unterminated end pre tag at index ${closeIndex + 1}")
                            val preTag = string.substring(closeIndex + 2, preCloseIndex).trim()
                            if (preTag.matches(Regex("/\\s*pre", RegexOption.IGNORE_CASE))) {
                                tokens += EndPre(null)
                                closeIndex = preCloseIndex
                            } else {
                                tokens += EndCode
                            }
                        } else tokens += if (isEnd) EndCode else StartCode
                    }
                    tag.equals("pre", ignoreCase = true) -> if (!isEnd && string.getOrNull(
                            closeIndex + 1
                        ) == '<'
                    ) {
                        val codeCloseIndex = string.indexOf('>', closeIndex + 2)
                        if (codeCloseIndex < 0) throw TextParseException("Unterminated code tag at index ${closeIndex + 1}")
                        val codeTag = string.substring(closeIndex + 2, codeCloseIndex).trim()
                        if (!codeTag.startsWith("code")) throw TextParseException("Expected code tag but found $codeTag at index ${closeIndex + 1}")
                        val attributes = codeTag.substring(4).trim()
                        val `class` = attributes.removePrefix("class=").removeSurrounding("\"")
                            .removeSurrounding("'")
                        tokens += StartPre(`class`)
                        closeIndex = codeCloseIndex
                    } else tokens += if (isEnd) EndPre(null) else StartPre(null)
                    else -> throw TextParseException("Unknown tag $tag at index $i")
                }
                i = closeIndex
            }
            '&' -> when {
                i + 4 <= string.length && string.substring(i + 1, i + 4) == "lt;" -> {
                    textBuilder.append('<')
                    i += 3
                }
                i + 4 <= string.length && string.substring(i + 1, i + 4) == "gt;" -> {
                    textBuilder.append('>')
                    i += 3
                }
                i + 6 <= string.length && string.substring(i + 1, i + 6) == "quot;" -> {
                    textBuilder.append('"')
                    i += 5
                }
                i + 5 <= string.length && string.substring(i + 1, i + 5) == "amp;" -> {
                    textBuilder.append('&')
                    i += 4
                }
                else -> textBuilder.append(c)
            }
            else -> textBuilder.append(c)
        }
        i++
    }
    if (textBuilder.isNotEmpty()) tokens += TextToken.Text(textBuilder.toString())
    return tokens.toText()
}
