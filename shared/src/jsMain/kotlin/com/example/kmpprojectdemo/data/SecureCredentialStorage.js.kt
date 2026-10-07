package com.example.kmpprojectdemo.data

import kotlinx.browser.localStorage

actual class SecureCredentialStorage actual constructor() {
    actual fun save(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    actual fun get(key: String): String? {
        return localStorage.getItem(key)
    }

    actual fun remove(key: String) {
        localStorage.removeItem(key)
    }
}
