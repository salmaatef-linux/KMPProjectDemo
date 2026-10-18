package com.example.kmpprojectdemo.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient
