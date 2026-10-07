package com.example.kmpprojectdemo.data

import kotlinx.browser.window
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.w3c.dom.events.Event

actual class ConnectivityChecker actual constructor() {
    private val _isOnline = MutableStateFlow(window.navigator.onLine)
    actual val isOnlineFlow: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val onlineHandler: (Event) -> Unit = { _ -> _isOnline.value = true }
    private val offlineHandler: (Event) -> Unit = { _ -> _isOnline.value = false }

    init {
        window.addEventListener("online", onlineHandler)
        window.addEventListener("offline", offlineHandler)
    }

    actual fun isOnline(): Boolean {
        return window.navigator.onLine
    }

    actual fun stopMonitoring() {
        window.removeEventListener("online", onlineHandler)
        window.removeEventListener("offline", offlineHandler)
    }
}
