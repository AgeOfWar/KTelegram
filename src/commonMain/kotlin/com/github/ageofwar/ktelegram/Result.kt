package com.github.ageofwar.ktelegram

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*
import kotlin.reflect.KClass

@Serializable(Result.Serializer::class)
sealed class Result<out T> {
    @Suppress("UNCHECKED_CAST")
    class Serializer<T>(private val serializer: KSerializer<T>) :
        JsonContentPolymorphicSerializer<Result<T>>(Result::class as KClass<Result<T>>) {
        override fun selectDeserializer(element: JsonElement) =
            when (element.jsonObject["ok"]!!.jsonPrimitive.boolean) {
                true -> Success.Serializer(serializer)
                false -> Error.Serializer
            }
    }
}

@Serializable(Success.Serializer::class)
data class Success<T>(val result: T) : Result<T>() {
    class Serializer<T>(private val serializer: KSerializer<T>) : KSerializer<Success<T>> {
        override val descriptor = buildClassSerialDescriptor("Success") {
            element<Boolean>("ok")
            element("result", serializer.descriptor)
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var result: T? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(decodeBooleanElement(descriptor, 0)) { "Not a Success!" }
                    1 -> result = decodeSerializableElement(descriptor, 1, serializer, result)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            @Suppress("UNCHECKED_CAST")
            Success(result as T)
        }

        override fun serialize(encoder: Encoder, value: Success<T>) {
            encoder.encodeStructure(descriptor) {
                encodeBooleanElement(descriptor, 0, true)
                encodeSerializableElement(descriptor, 1, serializer, value.result)
            }
        }
    }
}

@Serializable(Error.Serializer::class)
data class Error(
    @SerialName("error_code") val code: Int,
    val description: String? = null,
    val parameters: ResponseParameters? = null
) : Result<Nothing>() {
    object Serializer : KSerializer<Error> {
        override val descriptor = buildClassSerialDescriptor("Error") {
            element<Boolean>("ok")
            element<Int>("error_code")
            element<String?>("description")
            element<ResponseParameters?>("parameters")
        }

        @ExperimentalSerializationApi
        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var code: Int? = null
            var description: String? = null
            var parameters: ResponseParameters? = null
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> check(!decodeBooleanElement(descriptor, 0)) { "Not an Error!" }
                    1 -> code = decodeIntElement(descriptor, 1)
                    2 -> description = decodeStringElement(descriptor, 2)
                    3 -> parameters = decodeSerializableElement(
                        descriptor,
                        3,
                        ResponseParameters.serializer(),
                        parameters
                    )
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            requireNotNull(code)
            Error(code, description, parameters)
        }

        override fun serialize(encoder: Encoder, value: Error) {
            encoder.encodeStructure(descriptor) {
                val (code, description, parameters) = value
                encodeBooleanElement(descriptor, 0, false)
                encodeIntElement(descriptor, 1, code)
                if (description != null) encodeStringElement(descriptor, 2, description)
                if (parameters != null) encodeSerializableElement(
                    descriptor,
                    3,
                    ResponseParameters.serializer(),
                    parameters
                )
            }
        }
    }

    @Serializable(ResponseParameters.Serializer::class)
    sealed class ResponseParameters {
        @Serializable
        data class MigrateToChatId(
            @SerialName("migrate_to_chat_id") val migrateToChatId: Long
        ) : ResponseParameters()

        @Serializable
        data class RetryAfter(
            @SerialName("retry_after") val retryAfter: Int
        ) : ResponseParameters()

        object Serializer :
            JsonContentPolymorphicSerializer<ResponseParameters>(ResponseParameters::class) {
            override fun selectDeserializer(element: JsonElement): KSerializer<out ResponseParameters> {
                val json = element.jsonObject
                return when {
                    json.containsKey("migrate_to_chat_id") -> MigrateToChatId.serializer()
                    json.containsKey("retry_after") -> RetryAfter.serializer()
                    else -> throw SerializationException("Unknown ResponseParameters '$json'")
                }
            }
        }
    }
}

fun <T> Result<T>.unwrap() = when (this) {
    is Success<T> -> result
    is Error -> throw TelegramException(description ?: "No description", code, parameters)
}
