package com.github.ageofwar.ktelegram

import com.github.ageofwar.ktelegram.json.json
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlin.reflect.typeOf

@Serializable
data class LabeledPrice(
    val label: String,
    val amount: Long
)

@Serializable
data class Invoice(
    val title: String,
    val description: String,
    @SerialName("start_parameter") val startParameter: String,
    val currency: String,
    @SerialName("total_amount") val totalAmount: Long
)

@Serializable
data class ShippingAddress(
    @SerialName("country_code") val countryCode: String,
    val state: String,
    val city: String,
    @SerialName("street_line1") val streetLine1: String,
    @SerialName("street_line2") val streetLine2: String,
    @SerialName("post_code") val postCode: String,
)

@Serializable
data class OrderInfo(
    val name: String? = null,
    @SerialName("phone_number") val phoneNumber: String? = null,
    val email: String? = null,
    @SerialName("shipping_address") val shippingAddress: ShippingAddress? = null
)

@Serializable
data class ShippingOption(
    val id: String,
    val title: String,
    val prices: List<LabeledPrice>
)

@Serializable
data class SuccessfulPayment(
    val currency: String,
    @SerialName("total_amount") val totalAmount: Long,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("shipping_option_id") val shippingOptionId: String? = null,
    @SerialName("order_info") val orderInfo: OrderInfo? = null,
    @SerialName("telegram_payment_charge_id") val telegramPaymentChargeId: String,
    @SerialName("provider_payment_charge_id") val providerPaymentChargeId: String
)

@Serializable
data class ShippingQuery(
    val id: String,
    val from: User,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("shipping_address") val shippingAddress: ShippingAddress
) {
    val sender get() = from
}

@Serializable
data class PreCheckoutQuery(
    val id: String,
    val from: User,
    val currency: String,
    @SerialName("total_amount") val totalAmount: Long,
    @SerialName("invoice_payload") val invoicePayload: String,
    @SerialName("shipping_option_id") val shippingOptionId: String? = null,
    @SerialName("order_info") val orderInfo: OrderInfo? = null
) {
    val sender get() = from
}

@Serializable
data class Currency(
    val code: String,
    val title: String,
    val symbol: String,
    val native: String,
    @SerialName("thousands_sep") val thousandsSeparator: String,
    @SerialName("decimal_sep") val decimalSeparator: String,
    @SerialName("symbol_left") val symbolLeft: Boolean,
    @SerialName("space_between") val spaceBetween: Boolean,
    @SerialName("exp") val exponent: Int,
    @SerialName("min_amount") val minAmount: Long,
    @SerialName("max_amount") val maxAmount: Long
) {
    fun format(amount: Long, native: Boolean = true) = buildString {
        val symbol = if (native) this@Currency.native else symbol
        if (symbolLeft) append(symbol)
        if (spaceBetween && symbolLeft) append(' ')
        val stringAmount = amount.toString()
        val len = stringAmount.length
        if (exponent >= len) append('0') else {
            val intLen = len - exponent
            repeat(intLen) {
                if (it != 0 && it % 3 == intLen % 3) append(thousandsSeparator)
                append(stringAmount[it])
            }
        }
        if (exponent > 0) append(decimalSeparator)
        repeat(exponent - len) {
            append('0')
        }
        append(stringAmount.substring((len - exponent).coerceAtLeast(0)))
        if (spaceBetween && !symbolLeft) append(' ')
        if (!symbolLeft) append(symbol)
    }

    companion object {
        private val currencies = mutableMapOf<String, Currency>()

        suspend fun loadDefaults() {
            val response = try {
                defaultHttpClient().use {
                    it.get("https://core.telegram.org/bots/payments/currencies.json")
                }
            }  catch (e: ClientRequestException) {
                e.response
            }.bodyAsText()
            return try {
                currencies += json.decodeFromString<Map<String, Currency>>(response)
            } catch (e: Throwable) {
                throw SerializationException("An error occurred while deserializing $response to ${typeOf<Map<String, Currency>>()}", e)
            }
        }

        fun fromCode(code: String) = currencies[code]
        fun getSupportedCurrencies(): Set<String> = currencies.keys.toSet()
        fun format(currency: String, amount: Long, native: Boolean = true) = fromCode(currency)?.format(amount, native)
    }
}