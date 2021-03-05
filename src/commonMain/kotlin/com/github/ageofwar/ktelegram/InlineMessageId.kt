package com.github.ageofwar.ktelegram

data class InlineMessageId internal constructor(
    val messageId: MessageId? = null,
    val inlineId: String? = null
) {
    companion object {
        fun fromMessageId(messageId: MessageId) = InlineMessageId(messageId = messageId)
        fun fromInlineId(inlineId: String) = InlineMessageId(inlineId = inlineId)
    }
}
