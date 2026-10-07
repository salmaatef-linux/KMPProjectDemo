package com.example.kmpprojectdemo.data

import kotlinx.coroutines.flow.StateFlow

class AuthRepository(
    private val remoteDataSource: SapRemoteDataSource = SapRemoteDataSource(),
    private val csrfTokenManager: CsrfTokenManager = CsrfTokenManager(),
    private val localAuthDataSource: LocalAuthDataSource = LocalAuthDataSource(),
    private val connectivityChecker: ConnectivityChecker = ConnectivityChecker()
) {
    val activeCsrfToken: String?
        get() = csrfTokenManager.csrfToken

    val isOnlineFlow: StateFlow<Boolean>
        get() = connectivityChecker.isOnlineFlow

    fun isOnline(): Boolean {
        return connectivityChecker.isOnline()
    }

    fun stopMonitoring() {
        connectivityChecker.stopMonitoring()
    }

    suspend fun authenticate(username: String, password: String): Result<String> {
        val online = isOnline()

        if (online) {
            // Online Mode: must authenticate against SAP. Do NOT silently fall back to offline when online mode is active.
            val remoteResult = runCatching {
                val token = remoteDataSource.fetchCsrfToken(username, password)
                csrfTokenManager.updateToken(token)
                token
            }

            if (remoteResult.isSuccess) {
                // Successful Online login: register/update secure local authentication verifier (never store raw password)
                val salt = PasswordUtils.generateSalt()
                val hash = PasswordUtils.hashPassword(password, salt)
                localAuthDataSource.saveUser(username, hash, salt)
                return remoteResult
            } else {
                val exception = remoteResult.exceptionOrNull()
                val message = exception?.message ?: ""
                val isNetworkOrVpnError = message.contains("Failed to fetch", true) ||
                        message.contains("ConnectException", true) ||
                        message.contains("SocketTimeout", true) ||
                        message.contains("UnknownHost", true) ||
                        message.contains("Network", true) ||
                        message.contains("timeout", true)

                if (isNetworkOrVpnError) {
                    return Result.failure(Exception("Unable to connect to the SAP system. Please check your network connection and VPN, then try again."))
                } else {
                    return Result.failure(Exception("Unable to sign in. Please check your username and password and try again."))
                }
            }
        } else {
            // Offline Mode: authenticate locally using secure credential storage
            val localRecord = localAuthDataSource.getUser(username)
            if (localRecord == null) {
                return Result.failure(Exception("This user has not logged in online on this device yet. Please connect to the network and log in online first."))
            } else {
                val computedHash = PasswordUtils.hashPassword(password, localRecord.salt)
                if (computedHash == localRecord.passwordHash) {
                    return Result.success("OFFLINE_SESSION_TOKEN")
                } else {
                    return Result.failure(Exception("Incorrect username or password."))
                }
            }
        }
    }

    fun clearSession() {
        csrfTokenManager.clearToken()
    }
}
