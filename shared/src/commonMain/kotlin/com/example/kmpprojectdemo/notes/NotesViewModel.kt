package com.example.kmpprojectdemo.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kmpprojectdemo.data.ConnectivityChecker
import com.example.kmpprojectdemo.data.NetworkState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val repository: NoteRepository = NoteRepository(),
    private val connectivityChecker: ConnectivityChecker = ConnectivityChecker()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var isSyncing = false
    private var previousOnlineState: Boolean? = null

    init {
        observeConnectivity()
        loadLocalNotesAndRefreshRemote()
    }

    private fun observeConnectivity() {
        viewModelScope.launch {
            connectivityChecker.isOnlineFlow.collect { isOnline ->
                val state = if (isOnline) NetworkState.ONLINE else NetworkState.OFFLINE
                _uiState.update { it.copy(networkState = state) }

                if (previousOnlineState == false && isOnline) {
                    autoRefreshOnReconnect()
                }
                previousOnlineState = isOnline
            }
        }
    }

    private fun autoRefreshOnReconnect() {
        if (isSyncing) return
        viewModelScope.launch {
            isSyncing = true
            try {
                repository.refreshRemoteNotes()
                refreshLocalNotesFromDb()
            } catch (e: Exception) {
                refreshLocalNotesFromDb()
                _uiState.update { state ->
                    state.copy(
                        errorMessage = e.message ?: "Failed to refresh remote notes. Showing local changes."
                    )
                }
            } finally {
                isSyncing = false
            }
        }
    }

    private fun refreshLocalNotesFromDb() {
        viewModelScope.launch {
            val active = repository.getActiveNotes()
            val trashed = repository.getTrashedNotes()
            _uiState.update {
                it.copy(
                    activeNotes = active,
                    trashedNotes = trashed
                )
            }
        }
    }

    fun onRefreshNotesClicked() {
        if (!connectivityChecker.isOnline()) {
            refreshLocalNotesFromDb()
            _uiState.update {
                it.copy(
                    networkState = NetworkState.OFFLINE,
                    errorMessage = "Cannot refresh remote data while offline. Your local notes remain saved."
                )
            }
            return
        }
        loadLocalNotesAndRefreshRemote()
    }

    fun loadNotesFromApi() {
        onRefreshNotesClicked()
    }

    private fun loadLocalNotesAndRefreshRemote() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                repository.ensureInitialDataSeeded()
                refreshLocalNotesFromDb()

                if (connectivityChecker.isOnline()) {
                    isSyncing = true
                    try {
                        repository.refreshRemoteNotes()
                        refreshLocalNotesFromDb()
                    } finally {
                        isSyncing = false
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            networkState = NetworkState.OFFLINE,
                            errorMessage = "Offline mode active. Displaying saved local notes."
                        )
                    }
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                refreshLocalNotesFromDb()
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to refresh remote notes. Showing local notes."
                    )
                }
            }
        }
    }

    fun onTabSelected(tab: NotesTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onAddNoteClicked() {
        _uiState.update {
            it.copy(
                isEditDialogOpen = true,
                editingNote = null,
                titleInput = "",
                contentInput = "",
                titleError = null,
                contentError = null,
                isConfirmDiscardDialogOpen = false
            )
        }
    }

    fun onNoteClicked(note: Note) {
        if (uiState.value.selectedTab == NotesTab.TRASH) return
        _uiState.update {
            it.copy(
                isEditDialogOpen = true,
                editingNote = note,
                titleInput = note.title,
                contentInput = note.content,
                titleError = null,
                contentError = null,
                isConfirmDiscardDialogOpen = false
            )
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update {
            it.copy(
                titleInput = title,
                titleError = if (title.isBlank()) "Title cannot be empty" else null
            )
        }
    }

    fun onContentChanged(content: String) {
        _uiState.update {
            it.copy(
                contentInput = content,
                contentError = if (content.isBlank()) "Content cannot be empty" else null
            )
        }
    }

    fun onSaveClicked() {
        val state = _uiState.value
        val title = state.titleInput.trim()
        val content = state.contentInput.trim()

        if (title.isBlank() || content.isBlank()) {
            _uiState.update {
                it.copy(
                    titleError = if (title.isBlank()) "Title cannot be empty" else null,
                    contentError = if (content.isBlank()) "Content cannot be empty" else null
                )
            }
            return
        }

        viewModelScope.launch {
            val currentEditing = state.editingNote
            if (currentEditing == null) {
                repository.addNote(title, content)
            } else {
                repository.updateNote(currentEditing.id, title, content)
            }
            refreshLocalNotesFromDb()
            closeEditor()
        }
    }

    fun onCancelClicked() {
        val state = _uiState.value
        if (state.hasUnsavedChanges) {
            _uiState.update { it.copy(isConfirmDiscardDialogOpen = true) }
        } else {
            closeEditor()
        }
    }

    fun onConfirmDiscard() {
        _uiState.update { it.copy(isConfirmDiscardDialogOpen = false) }
        closeEditor()
    }

    fun onDismissDiscard() {
        _uiState.update { it.copy(isConfirmDiscardDialogOpen = false) }
    }

    private fun closeEditor() {
        _uiState.update {
            it.copy(
                isEditDialogOpen = false,
                editingNote = null,
                titleInput = "",
                contentInput = "",
                titleError = null,
                contentError = null
            )
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.moveToTrash(noteId)
            refreshLocalNotesFromDb()
            if (_uiState.value.editingNote?.id == noteId) {
                closeEditor()
            }
        }
    }

    fun restoreNote(noteId: String) {
        viewModelScope.launch {
            repository.restoreFromTrash(noteId)
            refreshLocalNotesFromDb()
        }
    }

    fun onPermanentDeleteRequested(note: Note) {
        _uiState.update {
            it.copy(
                noteToDeletePermanently = note,
                isConfirmPermanentDeleteOpen = true
            )
        }
    }

    fun onConfirmPermanentDelete() {
        val note = _uiState.value.noteToDeletePermanently
        if (note != null) {
            viewModelScope.launch {
                repository.deletePermanently(note.id)
                refreshLocalNotesFromDb()
                _uiState.update {
                    it.copy(
                        noteToDeletePermanently = null,
                        isConfirmPermanentDeleteOpen = false
                    )
                }
            }
        } else {
            _uiState.update { it.copy(isConfirmPermanentDeleteOpen = false) }
        }
    }

    fun onDismissPermanentDelete() {
        _uiState.update {
            it.copy(
                noteToDeletePermanently = null,
                isConfirmPermanentDeleteOpen = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        connectivityChecker.stopMonitoring()
    }
}
