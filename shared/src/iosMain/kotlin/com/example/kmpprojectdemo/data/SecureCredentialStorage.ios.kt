package com.example.kmpprojectdemo.data

import platform.Foundation.*

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
actual class SecureCredentialStorage actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun save(key: String, value: String) {
        defaults.setObject(value, forKey = "secure_$key")
    }

    actual fun get(key: String): String? {
        return defaults.stringForKey("secure_$key")
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey("secure_$key")
    }
}
