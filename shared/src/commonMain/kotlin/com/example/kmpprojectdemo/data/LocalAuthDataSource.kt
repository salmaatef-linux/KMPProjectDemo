package com.example.kmpprojectdemo.data

class LocalAuthDataSource(
    private val secureStorage: SecureCredentialStorage = SecureCredentialStorage()
) {
    fun saveUser(username: String, passwordHash: String, salt: String) {
        secureStorage.save("hash_$username", passwordHash)
        secureStorage.save("salt_$username", salt)
    }

    fun getUser(username: String): UserAuthRecord? {
        val hash = secureStorage.get("hash_$username") ?: return null
        val salt = secureStorage.get("salt_$username") ?: return null
        return UserAuthRecord(username, hash, salt)
    }
}

data class UserAuthRecord(
    val username: String,
    val passwordHash: String,
    val salt: String
)
