package com.example.kmpprojectdemo.notes

import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String,
    val title: String,
    val content: String,
    val timestamp: String? = null,
    val isRemote: Boolean = false,
    val isLocallyModified: Boolean = false,
    val isDeleted: Boolean = false,
    val isTombstone: Boolean = false,
    val updatedAt: Long = 0L,
    val createdAt: Long = 0L
)
