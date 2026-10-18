package com.example.kmpprojectdemo.notes

import com.example.kmpprojectdemo.utils.currentTimeMillis

open class InMemoryNotesLocalDataSource : NotesLocalDataSource {
    protected val notesMap = mutableMapOf<String, Note>()

    override suspend fun getActiveNotes(): List<Note> {
        return notesMap.values
            .filter { !it.isDeleted && !it.isTombstone }
            .sortedWith(compareByDescending<Note> { it.updatedAt }.thenByDescending { it.id })
    }

    override suspend fun getTrashedNotes(): List<Note> {
        return notesMap.values
            .filter { it.isDeleted && !it.isTombstone }
            .sortedWith(compareByDescending<Note> { it.updatedAt }.thenByDescending { it.id })
    }

    override suspend fun getNoteById(id: String): Note? {
        return notesMap[id]
    }

    override suspend fun saveNote(note: Note) {
        notesMap[note.id] = note
    }

    override suspend fun saveNotes(notes: List<Note>) {
        notes.forEach { notesMap[it.id] = it }
    }

    override suspend fun moveToTrash(id: String) {
        val existing = notesMap[id] ?: return
        notesMap[id] = existing.copy(isDeleted = true, updatedAt = currentTimeMillis())
    }

    override suspend fun restoreFromTrash(id: String) {
        val existing = notesMap[id] ?: return
        notesMap[id] = existing.copy(isDeleted = false, updatedAt = currentTimeMillis())
    }

    override suspend fun markTombstone(id: String) {
        val existing = notesMap[id]
        if (existing != null) {
            notesMap[id] = existing.copy(isTombstone = true, isDeleted = false, updatedAt = currentTimeMillis())
        } else {
            notesMap[id] = Note(
                id = id,
                title = "",
                content = "",
                isTombstone = true,
                updatedAt = currentTimeMillis()
            )
        }
    }

    override suspend fun deletePermanently(id: String) {
        val existing = notesMap[id]
        if (existing != null && existing.isRemote) {
            markTombstone(id)
        } else {
            notesMap.remove(id)
        }
    }

    override suspend fun clearAll() {
        notesMap.clear()
    }
}
