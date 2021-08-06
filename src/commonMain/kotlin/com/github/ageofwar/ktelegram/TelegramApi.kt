package com.github.ageofwar.ktelegram

import com.github.ageofwar.ktelegram.json.json
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class TelegramApi private constructor(
    private val apiUrl: String = "https://api.telegram.org",
    private val token: String,
    private val httpClient: HttpClient
) : Closeable by httpClient {

    constructor(token: String, apiUrl: String = "https://api.telegram.org") : this(
        apiUrl,
        token,
        defaultHttpClient()
    )

    suspend fun <T> call(
        method: String,
        returnType: KType,
        parameters: Map<String, Any?> = mapOf(),
        files: Map<String, OutputFile?> = mapOf()
    ): T {
        val response = try {
            httpClient.post("$apiUrl/bot$token/$method") {
                if (files.all { it.value == null }) {
                    parameters.forEach { (key, value) ->
                        parameter(key, value)
                    }
                } else {
                    body = MultiPartFormDataContent(formData {
                        files.forEach { (key, value) ->
                            if (value?.content != null && value.fileName != null) {
                                val content = value.content.invoke()
                                appendInput(
                                    key,
                                    Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=${value.fileName}"
                                        )
                                    },
                                    content.size.toLong()
                                ) {
                                    buildPacket {
                                        writeFully(content)
                                    }
                                }
                            }
                        }
                        parameters.forEach { (key, value) ->
                            if (value != null) {
                                append(key, value.toString())
                            }
                        }
                    })
                }
            }
        } catch (e: ClientRequestException) {
            e.response.readText()
        }
        @Suppress("UNCHECKED_CAST")
        return try {
            json.decodeFromString(json.serializersModule.serializer(returnType), response) as T
        } catch (e: Throwable) {
            throw SerializationException("An error occurred while deserializing $response to $returnType", e)
        }
    }

    suspend inline fun <reified T> call(
        method: String,
        parameters: Map<String, Any?> = mapOf(),
        files: Map<String, OutputFile?> = mapOf()
    ): T = try {
        call<Result<T>>(method, typeOf<Result<T>>(), parameters, files).unwrap()
    } catch (e: TelegramException) {
        when (val responseParameters = e.parameters) {
            is Error.ResponseParameters.RetryAfter -> {
                delay(responseParameters.retryAfter * 1000L)
                call<Result<T>>(method, typeOf<Result<T>>(), parameters, files).unwrap()
            }
            is Error.ResponseParameters.MigrateToChatId -> {
                call<Result<T>>(
                    method,
                    typeOf<Result<T>>(),
                    parameters + ("chat_id" to responseParameters.migrateToChatId),
                    files
                ).unwrap()
            }
            else -> throw e
        }
    }

    @Deprecated("Use request(String, KType, Map<String, Any?>, Map<String, OutputFile?>) instead", ReplaceWith("request(String, KType, Map<String, Any?>, Map<String, OutputFile?>)"))
    suspend fun <T> request(
        method: String,
        returnType: KType,
        parameters: Map<String, Any?> = mapOf(),
        files: Map<String, ByteArray?>
    ): T {
        val response = try {
            httpClient.post("$apiUrl/bot$token/$method") {
                if (files.all { it.value == null }) {
                    parameters.forEach { (key, value) ->
                        parameter(key, value)
                    }
                } else {
                    body = MultiPartFormDataContent(formData {
                        files.forEach { (key, value) ->
                            if (value != null) {
                                appendInput(
                                    key,
                                    Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=$key"
                                        )
                                    },
                                    value.size.toLong()
                                ) {
                                    buildPacket {
                                        writeFully(value)
                                    }
                                }
                            }
                        }
                        parameters.forEach { (key, value) ->
                            if (value != null) {
                                append(key, value.toString())
                            }
                        }
                    })
                }
            }
        } catch (e: ClientRequestException) {
            e.response.readText()
        }
        @Suppress("UNCHECKED_CAST")
        return try {
            json.decodeFromString(json.serializersModule.serializer(returnType), response) as T
        } catch (e: Throwable) {
            throw SerializationException("An error occurred while deserializing $response to $returnType", e)
        }
    }

    @Deprecated("Use request(String, Map<String, Any?>, Map<String, OutputFile?>) instead", ReplaceWith("request(String, Map<String, Any?>, Map<String, OutputFile?>)"))
    suspend inline fun <reified T> request(
        method: String,
        parameters: Map<String, Any?> = mapOf(),
        files: Map<String, ByteArray?>
    ): T = try {
        request<Result<T>>(method, typeOf<Result<T>>(), parameters, files).unwrap()
    } catch (e: TelegramException) {
        when (val responseParameters = e.parameters) {
            is Error.ResponseParameters.RetryAfter -> {
                delay(responseParameters.retryAfter * 1000L)
                request<Result<T>>(method, typeOf<Result<T>>(), parameters, files).unwrap()
            }
            is Error.ResponseParameters.MigrateToChatId -> {
                request<Result<T>>(
                    method,
                    typeOf<Result<T>>(),
                    parameters + ("chat_id" to responseParameters.migrateToChatId),
                    files
                ).unwrap()
            }
            else -> throw e
        }
    }

    suspend fun download(path: String): ByteArray {
        return httpClient.get("$apiUrl/file/bot$token/$path")
    }

    companion object
}

expect fun defaultHttpClient(): HttpClient

class TelegramException(
    val description: String,
    val errorCode: Int,
    val parameters: Error.ResponseParameters?
) : Exception("$errorCode $description")
