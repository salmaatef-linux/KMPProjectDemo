package com.example.kmpprojectdemo.notes

import com.example.kmpprojectdemo.data.NetworkState

enum class NotesTab {
    ACTIVE,
    TRASH
}

data class NotesUiState(
    val selectedTab: NotesTab = NotesTab.ACTIVE,
    val activeNotes: List<Note> = emptyList(),
    val trashedNotes: List<Note> = emptyList(),
    val isEditDialogOpen: Boolean = false,
    val editingNote: Note? = null,
    val titleInput: String = "",
    val contentInput: String = "",
    val titleError: String? = null,
    val contentError: String? = null,
    val isConfirmDiscardDialogOpen: Boolean = false,
    val noteToDeletePermanently: Note? = null,
    val isConfirmPermanentDeleteOpen: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val networkState: NetworkState = NetworkState.ONLINE
) {
    val notes: List<Note>
        get() = if (selectedTab == NotesTab.ACTIVE) activeNotes else trashedNotes

    val hasUnsavedChanges: Boolean
        get() = if (editingNote == null) {
            titleInput.isNotBlank() || contentInput.isNotBlank()
        } else {
            titleInput != editingNote.title || contentInput != editingNote.content
        }
}
