package com.example.kmpprojectdemo.notes

interface NotesLocalDataSource {
    suspend fun getActiveNotes(): List<Note>
    suspend fun getTrashedNotes(): List<Note>
    suspend fun getNoteById(id: String): Note?
    suspend fun saveNote(note: Note)
    suspend fun saveNotes(notes: List<Note>)
    suspend fun moveToTrash(id: String)
    suspend fun restoreFromTrash(id: String)
    suspend fun markTombstone(id: String)
    suspend fun deletePermanently(id: String)
    suspend fun clearAll()
}

expect fun createNotesLocalDataSource(): NotesLocalDataSource
