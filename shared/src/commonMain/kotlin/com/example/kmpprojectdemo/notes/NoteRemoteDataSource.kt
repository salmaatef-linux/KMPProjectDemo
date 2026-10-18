package com.example.kmpprojectdemo.notes

import com.example.kmpprojectdemo.data.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class DummyPostsResponse(
    val posts: List<DummyPost>,
    val total: Int = 0,
    val skip: Int = 0,
    val limit: Int = 0
)

@Serializable
data class DummyPost(
    val id: Int,
    val title: String,
    val body: String
)

open class NoteRemoteDataSource(
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
        const val ENDPOINT = "https://dummyjson.com/posts?limit=10"
    }

    open suspend fun fetchNotes(): List<Note> {
        val response: DummyPostsResponse = client.get(ENDPOINT).body()
        return response.posts.map { post ->
            Note(
                id = "remote_${post.id}",
                title = post.title,
                content = post.body,
                timestamp = null,
                isRemote = true
            )
        }
    }
}
