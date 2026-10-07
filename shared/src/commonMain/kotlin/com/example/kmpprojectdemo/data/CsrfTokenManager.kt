package com.example.kmpprojectdemo.data

class CsrfTokenManager {
    var csrfToken: String? = null
        private set

    fun updateToken(token: String?) {
        if (!token.isNullOrBlank() && !token.equals("fetch", ignoreCase = true)) {
            csrfToken = token
        }
    }

    fun clearToken() {
        csrfToken = null
    }
}
