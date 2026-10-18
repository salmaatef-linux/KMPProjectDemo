package com.example.kmpprojectdemo.notes

import kotlinx.browser.localStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsBrowserNotesLocalDataSource : InMemoryNotesLocalDataSource() {
    private val STORAGE_KEY = "smart_notes_db"

    init {
        try {
            val jsonString = localStorage.getItem(STORAGE_KEY)
            if (!jsonString.isNullOrBlank()) {
                val list = Json.decodeFromString<List<Note>>(jsonString)
                list.forEach { notesMap[it.id] = it }
            }
        } catch (_: Exception) {}
    }

    private fun persist() {
        try {
            val jsonString = Json.encodeToString(notesMap.values.toList())
            localStorage.setItem(STORAGE_KEY, jsonString)
        } catch (_: Exception) {}
    }

    override suspend fun saveNote(note: Note) {
        super.saveNote(note)
        persist()
    }

    override suspend fun saveNotes(notes: List<Note>) {
        super.saveNotes(notes)
        persist()
    }

    override suspend fun moveToTrash(id: String) {
        super.moveToTrash(id)
        persist()
    }

    override suspend fun restoreFromTrash(id: String) {
        super.restoreFromTrash(id)
        persist()
    }

    override suspend fun markTombstone(id: String) {
        super.markTombstone(id)
        persist()
    }

    override suspend fun deletePermanently(id: String) {
        super.deletePermanently(id)
        persist()
    }

    override suspend fun clearAll() {
        super.clearAll()
        persist()
    }
}

actual fun createNotesLocalDataSource(): NotesLocalDataSource = JsBrowserNotesLocalDataSource()
