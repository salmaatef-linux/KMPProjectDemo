package com.example.kmpprojectdemo.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class SapRemoteDataSource(
    private val client: HttpClient = HttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
        }
    }
) {
    companion object {
        const val SAP_ODATA_URL = "http://10.20.20.14:8000/sap/opu/odata/sap/zgw_mobile_app_get_mvt_srv/?sap-client=120"
        const val HEADER_X_CSRF_TOKEN = "X-CSRF-Token"
        const val CSRF_FETCH_VALUE = "Fetch"
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun fetchCsrfToken(username: String = "", password: String = ""): String {
        val hasBasicAuth = username.isNotBlank() && password.isNotBlank()
        val hasCsrfFetchHeader = true

        SapLogger.logRequestStart(
            url = SAP_ODATA_URL,
            method = "GET",
            hasBasicAuth = hasBasicAuth,
            hasCsrfFetchHeader = hasCsrfFetchHeader
        )

        try {
            val response: HttpResponse = client.get(SAP_ODATA_URL) {
                header(HEADER_X_CSRF_TOKEN, CSRF_FETCH_VALUE)
                if (hasBasicAuth) {
                    val credentials = "$username:$password"
                    val encodedCredentials = Base64.encode(credentials.encodeToByteArray())
                    header(HttpHeaders.Authorization, "Basic $encodedCredentials")
                }
            }

            val token = response.headers[HEADER_X_CSRF_TOKEN]
                ?: response.headers[HEADER_X_CSRF_TOKEN.lowercase()]

            SapLogger.logResponseSuccess(response, token)

            if (token.isNullOrBlank()) {
                throw IllegalStateException("SAP Gateway returned HTTP ${response.status.value}, but no 'X-CSRF-Token' header was present in response headers.")
            }

            return token
        } catch (e: Throwable) {
            SapLogger.logRequestError(
                url = SAP_ODATA_URL,
                method = "GET",
                hasBasicAuth = hasBasicAuth,
                hasCsrfFetchHeader = hasCsrfFetchHeader,
                throwable = e
            )
            throw e
        }
    }
}
