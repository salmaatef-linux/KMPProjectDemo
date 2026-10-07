package com.example.kmpprojectdemo.data

expect class SecureCredentialStorage() {
    fun save(key: String, value: String)
    fun get(key: String): String?
    fun remove(key: String)
}
