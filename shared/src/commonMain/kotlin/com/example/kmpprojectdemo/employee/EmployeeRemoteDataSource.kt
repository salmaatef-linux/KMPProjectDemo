package com.example.kmpprojectdemo.employee

import com.example.kmpprojectdemo.data.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class EmployeeRemoteDataSource(
    private val client: HttpClient = createHttpClient {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
) {
    companion object {
        const val ENDPOINT = "https://dummyjson.com/users/1"
    }

    suspend fun fetchEmployee(): EmployeeResponse {
        val response: EmployeeResponse = client.get(ENDPOINT).body()
        return response
    }
}
