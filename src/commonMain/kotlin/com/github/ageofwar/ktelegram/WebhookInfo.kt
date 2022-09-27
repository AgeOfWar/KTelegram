package com.github.ageofwar.ktelegram

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
data class WebhookInfo(
    val url: String,
    @SerialName("has_custom_certificate") val hasCustomCertificate: Boolean = false,
    @SerialName("pending_update_count") val pendingUpdateCount: Int = 0,
    @SerialName("ip_address") val ipAddress: String? = null,
    @SerialName("last_error_date") val lastErrorDate: Int? = null,
    @SerialName("last_error_message") val lastErrorMessage: String? = null,
    @SerialName("last_synchronization_error_date") val lastSynchronizationErrorDate: Long? = null,
    @SerialName("max_connections") val maxConnections: Int = 40,
    @Serializable(UpdateTypeSetSerializer::class)
    @SerialName("allowed_updates") val allowedUpdates: Set<KClass<out Update>> = emptySet()
)
