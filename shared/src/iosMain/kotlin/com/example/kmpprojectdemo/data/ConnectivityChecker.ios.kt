package com.example.kmpprojectdemo.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ConnectivityChecker actual constructor() {
    private val _isOnline = MutableStateFlow(true)
    actual val isOnlineFlow: StateFlow<Boolean> = _isOnline.asStateFlow()

    actual fun isOnline(): Boolean {
        return _isOnline.value
    }

    actual fun stopMonitoring() {
        // No-op for iOS baseline
    }
}
