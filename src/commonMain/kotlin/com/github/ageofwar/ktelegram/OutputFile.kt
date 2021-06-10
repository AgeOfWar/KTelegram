package com.github.ageofwar.ktelegram

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = OutputFile.Serializer::class)
data class OutputFile internal constructor(
    val fileId: String? = null,
    val url: String? = null,
    val fileName: String? = null,
    val content: (() -> ByteArray)? = null
) {
    companion object {
        fun fromFileId(fileId: String) = OutputFile(fileId = fileId)
        fun fromUrl(url: String) = OutputFile(url = url)
        fun fromContent(fileName: String, content: ByteArray) =
            OutputFile(fileName = fileName, content = { content })
        fun fromContent(fileName: String, content: () -> ByteArray) =
            OutputFile(fileName = fileName, content = content)
    }

    object Serializer : KSerializer<OutputFile> {
        override val descriptor = PrimitiveSerialDescriptor("UploadFile", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): OutputFile {
            val file = decoder.decodeString()
            return when {
                file.isFileId() -> fromFileId(file)
                else -> fromUrl(file)
            }
        }

        override fun serialize(encoder: Encoder, value: OutputFile) {
            when {
                value.fileId != null -> encoder.encodeString(value.fileId)
                value.url != null -> encoder.encodeString(value.url)
                value.content != null -> encoder.encodeString("attach://" + value.fileName)
            }
        }

        private fun String.isFileId() = all {
            it in 'A'..'Z' || it in 'a'..'z' || it in '0'..'9' || it == '_' || it == '-'
        }
    }
}
