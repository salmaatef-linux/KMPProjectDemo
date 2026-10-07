package com.example.kmpprojectdemo.data

import com.example.kmpprojectdemo.getPlatform
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request

object SapLogger {

    fun logRequestStart(
        url: String,
        method: String,
        hasBasicAuth: Boolean,
        hasCsrfFetchHeader: Boolean
    ) {
        val platformName = getPlatform().name
        println("==================================================")
        println("[SAP NETWORK LOG] [START] Platform: $platformName")
        println("Method: $method")
        println("URL: $url")
        println("Basic Auth Configured: $hasBasicAuth [CREDENTIALS REDACTED]")
        println("Header 'X-CSRF-Token: Fetch' Present: $hasCsrfFetchHeader")
        println("==================================================")
    }

    fun logResponseSuccess(response: HttpResponse, extractedToken: String?) {
        val platformName = getPlatform().name
        println("==================================================")
        println("[SAP NETWORK LOG] [RESPONSE SUCCESS] Platform: $platformName")
        println("URL: ${response.request.url}")
        println("HTTP Status Code: ${response.status.value} ${response.status.description}")
        
        println("Response Headers Received:")
        response.headers.forEach { key, values ->
            if (key.equals("Set-Cookie", ignoreCase = true)) {
                println("  $key: [REDACTED ${values.size} COOKIE(S)]")
            } else {
                println("  $key: ${values.joinToString(", ")}")
            }
        }
        
        val hasCsrfHeader = response.headers["X-CSRF-Token"] != null || response.headers["x-csrf-token"] != null
        println("X-CSRF-Token Header Present in Response: $hasCsrfHeader")
        println("CSRF Token Extracted Successfully: ${!extractedToken.isNullOrBlank()}")
        println("==================================================")
    }

    fun logRequestError(
        url: String,
        method: String,
        hasBasicAuth: Boolean,
        hasCsrfFetchHeader: Boolean,
        throwable: Throwable
    ) {
        val platformName = getPlatform().name
        println("==================================================")
        println("[SAP NETWORK LOG] [ERROR] Platform: $platformName")
        println("Method: $method")
        println("URL: $url")
        println("Basic Auth Configured: $hasBasicAuth [CREDENTIALS REDACTED]")
        println("Header 'X-CSRF-Token: Fetch' Present: $hasCsrfFetchHeader")
        println("Exception Type: ${throwable::class.simpleName ?: throwable::class.toString()}")
        println("Exception Message: ${throwable.message}")
        
        var cause = throwable.cause
        var depth = 1
        while (cause != null && depth <= 3) {
            println("  Caused by [$depth]: ${cause::class.simpleName}: ${cause.message}")
            cause = cause.cause
            depth++
        }

        val isWeb = platformName.contains("Web", ignoreCase = true) || 
                    platformName.contains("Chrome", ignoreCase = true) || 
                    platformName.contains("Js", ignoreCase = true)
        val isFetchError = throwable.message?.contains("Failed to fetch", ignoreCase = true) == true

        if (isWeb && isFetchError) {
            println("--------------------------------------------------")
            println("[DIAGNOSTIC ANALYSIS FOR WEB - BROWSER FETCH FAILURE]")
            println("The browser's fetch() API threw a generic 'Failed to fetch' TypeError.")
            println("In Web browsers (Kotlin/Wasm & JS), this occurs BEFORE an HTTP response is made available to JavaScript when:")
            println(" 1. CORS (Cross-Origin Resource Sharing) is blocked by the browser:")
            println("    - The web app origin (e.g. http://localhost:8080) differs from SAP Gateway (http://10.20.20.14:8000).")
            println("    - The browser sends an 'OPTIONS' preflight request for custom headers ('Authorization', 'X-CSRF-Token').")
            println("    - If SAP Gateway does NOT handle OPTIONS preflight or return 'Access-Control-Allow-Origin', browser blocks the request.")
            println(" 2. Mixed Content restrictions (if app is HTTPS and SAP is HTTP).")
            println(" 3. Direct network/socket reachability blocked at the browser level.")
            println("Note: The browser security model intentionally hides the HTTP status code/response from JavaScript during CORS blocks.")
            println("--------------------------------------------------")
        }
        println("==================================================")
    }
}
