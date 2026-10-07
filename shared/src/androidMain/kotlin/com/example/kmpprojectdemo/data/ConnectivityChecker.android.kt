package com.example.kmpprojectdemo.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

actual class ConnectivityChecker actual constructor() {
    private val _isOnline = MutableStateFlow(checkInitialOnline())
    actual val isOnlineFlow: StateFlow<Boolean> = _isOnline.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        startMonitoring()
    }

    actual fun isOnline(): Boolean {
        return checkInitialOnline()
    }

    actual fun stopMonitoring() {
        val context = AndroidAppContext.context as? Context ?: return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        if (networkCallback != null) {
            try {
                cm.unregisterNetworkCallback(networkCallback!!)
            } catch (e: Exception) {
                // Ignore unregistration errors
            }
            networkCallback = null
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkInitialOnline(): Boolean {
        val context = AndroidAppContext.context as? Context ?: return true
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
        return hasAnyValidInternetNetwork(cm)
    }

    @SuppressLint("MissingPermission")
    private fun hasAnyValidInternetNetwork(cm: ConnectivityManager, excludedNetwork: Network? = null): Boolean {
        val allNetworks = cm.allNetworks
        if (allNetworks.isEmpty()) return false
        for (network in allNetworks) {
            if (network == excludedNetwork) continue
            val caps = cm.getNetworkCapabilities(network) ?: continue
            if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun startMonitoring() {
        val context = AndroidAppContext.context as? Context ?: return
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return

        if (networkCallback != null) return

        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = hasAnyValidInternetNetwork(cm)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                _isOnline.value = hasAnyValidInternetNetwork(cm)
            }

            override fun onLost(network: Network) {
                _isOnline.value = hasAnyValidInternetNetwork(cm, excludedNetwork = network)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        try {
            cm.registerNetworkCallback(request, networkCallback!!)
        } catch (e: Exception) {
            // Ignore registration errors
        }
    }
}
