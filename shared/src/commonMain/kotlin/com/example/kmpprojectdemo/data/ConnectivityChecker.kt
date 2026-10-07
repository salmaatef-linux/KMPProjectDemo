package com.example.kmpprojectdemo.data

import kotlinx.coroutines.flow.StateFlow

expect class ConnectivityChecker() {
    fun isOnline(): Boolean
    val isOnlineFlow: StateFlow<Boolean>
    fun stopMonitoring()
}
