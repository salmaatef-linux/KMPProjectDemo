package com.example.kmpprojectdemo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform