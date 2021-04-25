package com.github.ageofwar.ktelegram.text

import com.github.ageofwar.ktelegram.MessageEntity
import com.github.ageofwar.ktelegram.Text

class TextBuilder(
    private val text: StringBuilder = StringBuilder(),
    private val entities: MutableList<MessageEntity> = mutableListOf()
) : CharSequence by text {
    fun append(text: Text) {
        if (text.isEmpty()) return
        text.entities.forEach {
            if (!mix(it)) entities.add(it.move(offset = length + it.offset))
        }
        this.text.append(text.text)
    }

    fun build() = Text(text.toString(), entities.toList())

    private fun mix(entity: MessageEntity): Boolean {
        var i = entities.lastIndex
        while (i >= 0) {
            if (entities[i].endIndex != length || entity.offset != 0) return false
            if (entities[i].typeEquals(entity)) {
                entities[i] = entities[i].move(length = entities[i].length + entity.length)
                return true
            }
            i--
        }
        return false
    }
}

fun buildText(builder: TextBuilder.() -> Unit): Text = TextBuilder().apply(builder).build()
