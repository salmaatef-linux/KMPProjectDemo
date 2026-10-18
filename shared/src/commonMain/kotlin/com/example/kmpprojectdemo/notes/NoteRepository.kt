package com.example.kmpprojectdemo.notes

import com.example.kmpprojectdemo.utils.currentTimeMillis
import com.example.kmpprojectdemo.utils.formatDate

class NoteRepository(
    private val localDataSource: NotesLocalDataSource = createNotesLocalDataSource(),
    private val remoteDataSource: NoteRemoteDataSource = NoteRemoteDataSource()
) {
    suspend fun ensureInitialDataSeeded() {
        val active = localDataSource.getActiveNotes()
        val trashed = localDataSource.getTrashedNotes()
        if (active.isEmpty() && trashed.isEmpty()) {
            val now = currentTimeMillis()
            val initialNotes = listOf(
                Note(
                    id = "1",
                    title = "Project Kickoff Ideas",
                    content = "Discuss Linux Plus brand integration, color palette specifications (#292260 and #ED235B), and cross-platform compatibility goals.",
                    timestamp = "Oct 15, 2026",
                    isRemote = false,
                    isLocallyModified = false,
                    createdAt = 1791984000000L,
                    updatedAt = now - 3000
                ),
                Note(
                    id = "2",
                    title = "Sprint Planning",
                    content = "Focus on Phase 1 UI and in-memory interactions. Keep state clean in commonMain without persistence or external DB dependencies for now.",
                    timestamp = "Oct 14, 2026",
                    isRemote = false,
                    isLocallyModified = false,
                    createdAt = 1791897600000L,
                    updatedAt = now - 2000
                ),
                Note(
                    id = "3",
                    title = "Design System Notes",
                    content = "Ensure cards and input surfaces are crisp white with dark readable text, and background is deep purple (#292260) with vibrant pink (#ED235B) buttons.",
                    timestamp = "Oct 12, 2026",
                    isRemote = false,
                    isLocallyModified = false,
                    createdAt = 1791724800000L,
                    updatedAt = now - 1000
                )
            )
            localDataSource.saveNotes(initialNotes)
        }
    }

    suspend fun getActiveNotes(): List<Note> {
        return localDataSource.getActiveNotes()
    }

    suspend fun getTrashedNotes(): List<Note> {
        return localDataSource.getTrashedNotes()
    }

    suspend fun refreshRemoteNotes() {
        val remoteNotes = remoteDataSource.fetchNotes()
        val now = currentTimeMillis()

        val notesToSave = mutableListOf<Note>()
        for (remoteNote in remoteNotes) {
            val local = localDataSource.getNoteById(remoteNote.id)
            if (local != null) {
                if (local.isTombstone) continue
                if (local.isLocallyModified || local.isDeleted) continue

                notesToSave.add(
                    local.copy(
                        title = remoteNote.title,
                        content = remoteNote.content,
                        timestamp = remoteNote.timestamp ?: local.timestamp ?: "Remote",
                        isRemote = true,
                        updatedAt = if (local.updatedAt == 0L) now else local.updatedAt
                    )
                )
            } else {
                notesToSave.add(
                    Note(
                        id = remoteNote.id,
                        title = remoteNote.title,
                        content = remoteNote.content,
                        timestamp = remoteNote.timestamp ?: "Remote",
                        isRemote = true,
                        isLocallyModified = false,
                        isDeleted = false,
                        isTombstone = false,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            }
        }

        if (notesToSave.isNotEmpty()) {
            localDataSource.saveNotes(notesToSave)
        }
    }

    suspend fun addNote(title: String, content: String): Note {
        val id = generateNoteId()
        val now = currentTimeMillis()
        val formattedDate = formatDate(now)
        val note = Note(
            id = id,
            title = title,
            content = content,
            timestamp = formattedDate,
            isRemote = false,
            isLocallyModified = true,
            isDeleted = false,
            isTombstone = false,
            createdAt = now,
            updatedAt = now
        )
        localDataSource.saveNote(note)
        return note
    }

    suspend fun updateNote(id: String, title: String, content: String) {
        val existing = localDataSource.getNoteById(id) ?: return
        val now = currentTimeMillis()
        val updated = existing.copy(
            title = title,
            content = content,
            isLocallyModified = true,
            updatedAt = now
        )
        localDataSource.saveNote(updated)
    }

    suspend fun moveToTrash(id: String) {
        localDataSource.moveToTrash(id)
    }

    suspend fun restoreFromTrash(id: String) {
        localDataSource.restoreFromTrash(id)
    }

    suspend fun deletePermanently(id: String) {
        localDataSource.deletePermanently(id)
    }

    private fun generateNoteId(): String {
        val time = currentTimeMillis()
        val random = (100000..999999).random()
        return "local_${time}_$random"
    }
}
