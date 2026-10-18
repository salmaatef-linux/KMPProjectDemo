package com.example.kmpprojectdemo.notes

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class NoteRepositoryTest {

    private fun createTestRepository(
        fakeRemoteNotes: List<Note> = emptyList(),
        shouldFailRemote: Boolean = false,
        existingLocalDataSource: InMemoryNotesLocalDataSource? = null
    ): Pair<NoteRepository, InMemoryNotesLocalDataSource> {
        val localDataSource = existingLocalDataSource ?: InMemoryNotesLocalDataSource()
        val fakeRemoteDataSource = object : NoteRemoteDataSource() {
            override suspend fun fetchNotes(): List<Note> {
                if (shouldFailRemote) {
                    throw Exception("Network request failed. Offline mode active.")
                }
                return fakeRemoteNotes
            }
        }
        val repository = NoteRepository(
            localDataSource = localDataSource,
            remoteDataSource = fakeRemoteDataSource
        )
        return Pair(repository, localDataSource)
    }

    @Test
    fun testInitialSeedingWhenEmpty() = runTest {
        val (repository, _) = createTestRepository()
        repository.ensureInitialDataSeeded()

        val active = repository.getActiveNotes()
        assertEquals(3, active.size)
        assertTrue(active.any { it.title == "Project Kickoff Ideas" })
        assertTrue(active.all { !it.isRemote })
    }

    @Test
    fun testOfflineStartupWithSavedNotes() = runTest {
        val localDs = InMemoryNotesLocalDataSource()
        localDs.saveNote(
            Note(
                id = "saved_1",
                title = "Offline Saved Note",
                content = "Offline Content",
                isRemote = false
            )
        )

        val (repository, _) = createTestRepository(
            shouldFailRemote = true,
            existingLocalDataSource = localDs
        )

        val active = repository.getActiveNotes()
        assertEquals(1, active.size)
        assertEquals("Offline Saved Note", active.first().title)
        assertFalse(active.first().isRemote)
    }

    @Test
    fun testCreatingAndEditingNotesOfflineAndPreservingCreationDate() = runTest {
        val (repository, _) = createTestRepository(shouldFailRemote = true)
        val created = repository.addNote("Offline Title", "Offline Content")

        assertFalse(created.isRemote)
        assertTrue(created.isLocallyModified)
        val initialCreatedAt = created.createdAt
        assertTrue(initialCreatedAt > 0L)

        val active = repository.getActiveNotes()
        assertTrue(active.any { it.id == created.id })

        repository.updateNote(created.id, "Updated Offline Title", "Updated Content")
        val updatedNote = repository.getActiveNotes().first { it.id == created.id }

        assertEquals("Updated Offline Title", updatedNote.title)
        assertEquals(initialCreatedAt, updatedNote.createdAt) // Creation date preserved!
        assertTrue(updatedNote.isLocallyModified)
    }

    @Test
    fun testOfflineTrashRestoreAndPermanentDelete() = runTest {
        val (repository, _) = createTestRepository(shouldFailRemote = true)
        repository.ensureInitialDataSeeded()

        val note = repository.getActiveNotes().first()
        repository.moveToTrash(note.id)

        assertFalse(repository.getActiveNotes().any { it.id == note.id })
        assertTrue(repository.getTrashedNotes().any { it.id == note.id })

        repository.restoreFromTrash(note.id)
        assertTrue(repository.getActiveNotes().any { it.id == note.id })

        repository.moveToTrash(note.id)
        repository.deletePermanently(note.id)

        assertFalse(repository.getActiveNotes().any { it.id == note.id })
        assertFalse(repository.getTrashedNotes().any { it.id == note.id })
    }

    @Test
    fun testPersistenceOfChangesAcrossRestarts() = runTest {
        val localDs = InMemoryNotesLocalDataSource()
        val (repo1, _) = createTestRepository(existingLocalDataSource = localDs)

        val note = repo1.addNote("Persistent Title", "Persistent Content")

        // Simulate app restart by initializing repo2 over the same local storage
        val (repo2, _) = createTestRepository(existingLocalDataSource = localDs)

        val loaded = repo2.getActiveNotes().firstOrNull { it.id == note.id }
        assertNotNull(loaded)
        assertEquals("Persistent Title", loaded.title)
        assertFalse(loaded.isRemote)
    }

    @Test
    fun testFailedNetworkRequestPreservesLocalChanges() = runTest {
        val (repository, _) = createTestRepository(shouldFailRemote = true)
        repository.addNote("Offline Note", "Offline Content")

        try {
            repository.refreshRemoteNotes()
        } catch (_: Exception) {
            // Network failure
        }

        val active = repository.getActiveNotes()
        assertEquals(1, active.size)
        assertEquals("Offline Note", active.first().title)
        assertFalse(active.first().isRemote)
    }

    @Test
    fun testPermanentDeleteTombstonePreventsResurrectionFromRemote() = runTest {
        val remotePost = Note(
            id = "100",
            title = "Remote Post 100",
            content = "Remote Content",
            isRemote = true
        )
        val (repository, localDs) = createTestRepository(fakeRemoteNotes = listOf(remotePost))

        repository.refreshRemoteNotes()
        assertEquals(1, repository.getActiveNotes().size)

        repository.moveToTrash("100")
        repository.deletePermanently("100")

        val tombstone = localDs.getNoteById("100")
        assertNotNull(tombstone)
        assertTrue(tombstone.isTombstone)
        assertTrue(repository.getActiveNotes().isEmpty())

        repository.refreshRemoteNotes()
        assertTrue(repository.getActiveNotes().isEmpty())
    }

    @Test
    fun testRemoteRefreshNeverOverwritesLocalEdits() = runTest {
        val remotePost = Note(
            id = "1",
            title = "Original Remote Title",
            content = "Original Remote Content",
            isRemote = true
        )
        val (repository, _) = createTestRepository(fakeRemoteNotes = listOf(remotePost))

        repository.ensureInitialDataSeeded()
        repository.updateNote("1", "Local Edited Title", "Local Edited Content")

        repository.refreshRemoteNotes()

        val active = repository.getActiveNotes()
        val note1 = active.first { it.id == "1" }

        assertEquals("Local Edited Title", note1.title)
        assertEquals("Local Edited Content", note1.content)
        assertTrue(note1.isLocallyModified)
    }

    @Test
    fun testDuplicateRefreshesDoNotDuplicateNotes() = runTest {
        val remotePost = Note(
            id = "200",
            title = "Remote 200",
            content = "Content 200",
            isRemote = true
        )
        val (repository, _) = createTestRepository(fakeRemoteNotes = listOf(remotePost))

        repository.refreshRemoteNotes()
        val countFirst = repository.getActiveNotes().size

        repository.refreshRemoteNotes()
        val countSecond = repository.getActiveNotes().size

        assertEquals(countFirst, countSecond)
        assertEquals(1, countSecond)
    }
}
