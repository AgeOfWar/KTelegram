package com.github.ageofwar.ktelegram

import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.features.*
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContextBuilder

actual fun defaultHttpClient() = HttpClient(Apache) {
    install(HttpTimeout)
    engine {
        customizeClient {
            setSSLContext(
                SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy())
                    .build()
            )
            setSSLHostnameVerifier(NoopHostnameVerifier())
        }
    }
}
