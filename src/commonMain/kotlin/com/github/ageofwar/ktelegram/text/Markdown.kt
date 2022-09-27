package com.github.ageofwar.ktelegram.text

import com.github.ageofwar.ktelegram.Text
import com.github.ageofwar.ktelegram.text.TextToken.*

fun Text.toMarkdown() = toString { it.toMarkdown() }

fun String.toMarkdown() = replace("\\", "\\\\").replace(Regex("[*_~|>#+-={}.!\\[\\]()`]"), "\\\\$0")

fun TextToken.toMarkdown() = when (this) {
    is TextToken.Text -> text.toMarkdown()
    is StartBold -> "*"
    is StartItalic -> "_"
    is StartUnderline -> "__"
    is StartStrikethrough -> "~"
    is StartCode -> "`"
    is StartPre -> if (language != null) "```${language}\n" else "```\n"
    is StartTextLink -> "["
    is StartTextMention -> "["
    is StartSpoiler -> "||"
    is EndBold -> "*"
    is EndItalic -> "_\r"
    is EndUnderline -> "__"
    is EndStrikethrough -> "~"
    is EndCode -> "`"
    is EndPre -> "\n```"
    is EndTextLink -> "](${url!!})"
    is EndTextMention -> "](${sender!!.url})"
    is EndSpoiler -> "||"
    else -> ""
}

fun String.parseMarkdown() = replace(Regex("\\\\([*_~|>#+-={}.!\\[\\]()`])"), "$1").replace("\\\\", "\\")

fun Text.Companion.parseMarkdown(string: String): Text {
    val tokens = mutableListOf<TextToken>()
    val startTokens = mutableListOf<TextToken>()
    var textBuilder = StringBuilder()
    var i = 0

    fun addText() {
        if (textBuilder.isNotEmpty()) {
            tokens += TextToken.Text(textBuilder.toString())
            textBuilder = StringBuilder()
        }
    }

    while (i < string.length) {
        when (val c = string[i]) {
            '*' -> {
                addText()
                if (startTokens.lastOrNull() is StartBold) {
                    tokens += EndBold
                    startTokens.removeLast()
                } else {
                    tokens += StartBold.also {
                        startTokens += it
                    }
                }
            }
            '_' -> {
                addText()
                if (string.getOrNull(i + 1) == '_') {
                    if (startTokens.lastOrNull() is StartUnderline) {
                        tokens += EndUnderline
                        startTokens.removeLast()
                    } else {
                        tokens += StartUnderline.also {
                            startTokens += it
                        }
                    }
                    i++
                } else {
                    if (startTokens.lastOrNull() is StartItalic) {
                        tokens += EndItalic
                        startTokens.removeLast()
                        if (string.getOrNull(i + 1) == '\r') i++
                    } else {
                        tokens += StartItalic.also {
                            startTokens += it
                        }
                    }
                }
            }
            '~' -> {
                addText()
                if (startTokens.lastOrNull() is StartStrikethrough) {
                    tokens += EndStrikethrough
                    startTokens.removeLast()
                } else {
                    tokens += StartStrikethrough.also {
                        startTokens += it
                    }
                }
            }
            '|' -> {
                addText()
                if (string.getOrNull(i + 1) != '|') throw TextParseException("Expected '|' to complete spoiler at index ${i + 1}")
                if (startTokens.lastOrNull() is StartSpoiler) {
                    tokens += EndSpoiler
                    startTokens.removeLast()
                } else {
                    tokens += StartSpoiler.also {
                        startTokens += it
                    }
                }
            }
            '`' -> {
                addText()
                if (string.getOrNull(i + 1) == '`' && string.getOrNull(i + 2) == '`') {
                    if (startTokens.lastOrNull() is StartPre) {
                        tokens += EndPre(null)
                        startTokens.removeLast()
                        i += 2
                    } else {
                        val closeIndex = string.indexOf('\n', i + 3)
                        if (closeIndex < 0) throw TextParseException("Unterminated pre at index $i")
                        val language = string.substring(i + 3, closeIndex).trim()
                        tokens += StartPre(if (language.isEmpty()) null else language).also {
                            startTokens += it
                        }
                        i = closeIndex
                    }
                } else {
                    if (startTokens.lastOrNull() is StartCode) {
                        tokens += EndCode
                        startTokens.removeLast()
                    } else {
                        tokens += StartCode.also {
                            startTokens += it
                        }
                    }
                }
            }
            '\n' -> {
                if (string.getOrNull(i + 1) != '`') textBuilder.append(c)
            }
            '[' -> {
                addText()
                tokens += StartTextLink(null).also {
                    startTokens += it
                }
            }
            ']' -> {
                addText()
                if (string.getOrNull(i + 1) != '(') throw TextParseException("Unterminated text link at index $i")
                val closeIndex = string.indexOf(')', i + 2)
                if (closeIndex < 0) throw TextParseException("Unterminated text link at index $i")
                val url = string.substring(i + 2, closeIndex).trim().parseMarkdown()
                tokens += EndTextLink(url)
                startTokens.removeLast()
                i = closeIndex
            }
            '\\' -> if (i < string.length) when (val escape = string[i + 1]) {
                '\\', '*', '_', '~', '`', '[', ']', '(', ')', '{', '}', '>', '#', '+', '-', '|', '.', '!', '=' -> {
                    textBuilder.append(escape)
                    i++
                }
                else -> throw TextParseException("Illegal escape of character $escape at index $i")
            } else throw TextParseException("Unterminated escape at index $i")
            else -> textBuilder.append(c)
        }
        i++
    }
    if (textBuilder.isNotEmpty()) tokens += TextToken.Text(textBuilder.toString())
    return tokens.toText()
}
