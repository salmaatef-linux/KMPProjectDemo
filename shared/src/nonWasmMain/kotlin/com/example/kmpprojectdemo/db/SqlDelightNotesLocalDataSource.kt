package com.example.kmpprojectdemo.db

import app.cash.sqldelight.db.SqlDriver
import com.example.kmpprojectdemo.notes.Note
import com.example.kmpprojectdemo.notes.NotesLocalDataSource
import com.example.kmpprojectdemo.utils.currentTimeMillis

class SqlDelightNotesLocalDataSource(
    driver: SqlDriver
) : NotesLocalDataSource {
    private val database = AppDatabase(driver)
    private val queries = database.appDatabaseQueries

    override suspend fun getActiveNotes(): List<Note> {
        return queries.selectAllActiveNotes().executeAsList().map { it.toNote() }
    }

    override suspend fun getTrashedNotes(): List<Note> {
        return queries.selectAllTrashedNotes().executeAsList().map { it.toNote() }
    }

    override suspend fun getNoteById(id: String): Note? {
        return queries.selectNoteById(id).executeAsOneOrNull()?.toNote()
    }

    override suspend fun saveNote(note: Note) {
        queries.insertNote(
            id = note.id,
            title = note.title,
            content = note.content,
            timestamp = note.timestamp,
            isRemote = if (note.isRemote) 1L else 0L,
            isLocallyModified = if (note.isLocallyModified) 1L else 0L,
            isDeleted = if (note.isDeleted) 1L else 0L,
            isTombstone = if (note.isTombstone) 1L else 0L,
            updatedAt = note.updatedAt,
            createdAt = note.createdAt
        )
    }

    override suspend fun saveNotes(notes: List<Note>) {
        database.transaction {
            notes.forEach { note ->
                queries.insertNote(
                    id = note.id,
                    title = note.title,
                    content = note.content,
                    timestamp = note.timestamp,
                    isRemote = if (note.isRemote) 1L else 0L,
                    isLocallyModified = if (note.isLocallyModified) 1L else 0L,
                    isDeleted = if (note.isDeleted) 1L else 0L,
                    isTombstone = if (note.isTombstone) 1L else 0L,
                    updatedAt = note.updatedAt,
                    createdAt = note.createdAt
                )
            }
        }
    }

    override suspend fun moveToTrash(id: String) {
        queries.setTrashState(isDeleted = 1L, updatedAt = currentTimeMillis(), id = id)
    }

    override suspend fun restoreFromTrash(id: String) {
        queries.setTrashState(isDeleted = 0L, updatedAt = currentTimeMillis(), id = id)
    }

    override suspend fun markTombstone(id: String) {
        queries.markTombstone(updatedAt = currentTimeMillis(), id = id)
    }

    override suspend fun deletePermanently(id: String) {
        val note = getNoteById(id)
        if (note != null && note.isRemote) {
            queries.markTombstone(updatedAt = currentTimeMillis(), id = id)
        } else {
            queries.deleteNoteById(id)
        }
    }

    override suspend fun clearAll() {
        queries.deleteAllNotes()
    }

    private fun NoteEntity.toNote(): Note {
        return Note(
            id = id,
            title = title,
            content = content,
            timestamp = timestamp,
            isRemote = isRemote == 1L,
            isLocallyModified = isLocallyModified == 1L,
            isDeleted = isDeleted == 1L,
            isTombstone = isTombstone == 1L,
            updatedAt = updatedAt,
            createdAt = createdAt
        )
    }
}
