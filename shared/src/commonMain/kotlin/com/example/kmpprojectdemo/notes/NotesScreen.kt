package com.example.kmpprojectdemo.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kmpprojectdemo.data.NetworkState
import com.example.kmpprojectdemo.utils.getDisplayDate

private val LinuxPlusBackground = Color(0xFF292260)
private val LinuxPlusAccent = Color(0xFFED235B)
private val SurfaceWhite = Color(0xFFFFFFFF)
private val TextDark = Color(0xFF1F1A42)
private val TextMuted = Color(0xFF6E6893)

@Composable
fun NotesScreen(
    viewModel: NotesViewModel = viewModel { NotesViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LinuxPlusBackground)
            .safeContentPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section: Title, Actions & Tab Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
            ) {
                // Row 1: Title & Subtitle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Smart Notes",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp
                            ),
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Your thoughts, organized",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 2: Refresh Action, Network Status & Notes Count Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.onRefreshNotesClicked() },
                        enabled = !uiState.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinuxPlusAccent,
                            contentColor = Color.White,
                            disabledContainerColor = LinuxPlusAccent.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.heightIn(min = 36.dp, max = 40.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "🔄", fontSize = 12.sp)
                                Text(
                                    text = "Refresh Notes",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Network Status Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when (uiState.networkState) {
                                NetworkState.ONLINE -> Color(0xFF2E7D32).copy(alpha = 0.2f)
                                NetworkState.OFFLINE -> Color(0xFFC62828).copy(alpha = 0.3f)
                                NetworkState.CHECKING -> Color(0xFFF57F17).copy(alpha = 0.3f)
                            }
                        ) {
                            Text(
                                text = when (uiState.networkState) {
                                    NetworkState.ONLINE -> "🟢 Online"
                                    NetworkState.OFFLINE -> "🔴 Offline"
                                    NetworkState.CHECKING -> "🟡 Checking"
                                },
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        // Notes Count Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "${uiState.notes.size} Notes",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 3: Tab Selector (Active vs Trash)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.onTabSelected(NotesTab.ACTIVE) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (uiState.selectedTab == NotesTab.ACTIVE) LinuxPlusAccent else Color.Transparent
                    ) {
                        Text(
                            text = "Active (${uiState.activeNotes.size})",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.onTabSelected(NotesTab.TRASH) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (uiState.selectedTab == NotesTab.TRASH) LinuxPlusAccent else Color.Transparent
                    ) {
                        Text(
                            text = "Trash (${uiState.trashedNotes.size})",
                            modifier = Modifier.padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner if present
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.onRefreshNotesClicked() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LinuxPlusAccent,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Retry", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Notes List or Empty State
            if (uiState.notes.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceWhite
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (uiState.selectedTab == NotesTab.ACTIVE) "📂" else "🗑️",
                                fontSize = 48.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.selectedTab == NotesTab.ACTIVE) "No active notes" else "Trash is empty",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.selectedTab == NotesTab.ACTIVE) {
                                    "Tap '+' below to add a note or refresh from API."
                                } else {
                                    "Deleted notes moved to Trash will appear here."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.notes.forEach { note ->
                        if (uiState.selectedTab == NotesTab.ACTIVE) {
                            NoteCard(
                                note = note,
                                onClick = { viewModel.onNoteClicked(note) },
                                onDelete = { viewModel.deleteNote(note.id) }
                            )
                        } else {
                            TrashNoteCard(
                                note = note,
                                onRestore = { viewModel.restoreNote(note.id) },
                                onDeletePermanently = { viewModel.onPermanentDeleteRequested(note) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }
        }

        // Floating Action Button to Add Note (Only in Active tab)
        if (uiState.selectedTab == NotesTab.ACTIVE) {
            FloatingActionButton(
                onClick = { viewModel.onAddNoteClicked() },
                containerColor = LinuxPlusAccent,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                )
            }
        }

        // Add / Edit Note Dialog
        if (uiState.isEditDialogOpen) {
            val isEditing = uiState.editingNote != null
            AlertDialog(
                onDismissRequest = { viewModel.onCancelClicked() },
                containerColor = SurfaceWhite,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEditing) "Edit Note" else "Add New Note",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextDark
                        )
                        if (isEditing) {
                            TextButton(
                                onClick = { viewModel.deleteNote(uiState.editingNote!!.id) },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Delete", color = LinuxPlusAccent)
                            }
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.titleInput,
                            onValueChange = { viewModel.onTitleChanged(it) },
                            label = { Text("Title") },
                            isError = uiState.titleError != null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = TextMuted.copy(alpha = 0.5f),
                                focusedLabelColor = LinuxPlusAccent,
                                cursorColor = LinuxPlusAccent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.titleError != null) {
                            Text(
                                text = uiState.titleError!!,
                                color = LinuxPlusAccent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        OutlinedTextField(
                            value = uiState.contentInput,
                            onValueChange = { viewModel.onContentChanged(it) },
                            label = { Text("Content") },
                            isError = uiState.contentError != null,
                            maxLines = 6,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LinuxPlusAccent,
                                unfocusedBorderColor = TextMuted.copy(alpha = 0.5f),
                                focusedLabelColor = LinuxPlusAccent,
                                cursorColor = LinuxPlusAccent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                        if (uiState.contentError != null) {
                            Text(
                                text = uiState.contentError!!,
                                color = LinuxPlusAccent,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onSaveClicked() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinuxPlusAccent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (isEditing) "Save Changes" else "Save Note")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onCancelClicked() }
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }

        // Confirm Discard Dialog
        if (uiState.isConfirmDiscardDialogOpen) {
            AlertDialog(
                onDismissRequest = { viewModel.onDismissDiscard() },
                containerColor = SurfaceWhite,
                title = {
                    Text(
                        text = "Discard Changes?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextDark
                    )
                },
                text = {
                    Text(
                        text = "You have unsaved changes. Are you sure you want to discard them?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onConfirmDiscard() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinuxPlusAccent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Discard")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDismissDiscard() }
                    ) {
                        Text("Keep Editing", color = TextMuted)
                    }
                }
            )
        }

        // Confirm Permanent Delete Dialog
        if (uiState.isConfirmPermanentDeleteOpen) {
            val note = uiState.noteToDeletePermanently
            AlertDialog(
                onDismissRequest = { viewModel.onDismissPermanentDelete() },
                containerColor = SurfaceWhite,
                title = {
                    Text(
                        text = "Permanently Delete Note?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = TextDark
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to permanently delete \"${note?.title ?: "this note"}\"? This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onConfirmPermanentDelete() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LinuxPlusAccent,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Delete Permanently")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { viewModel.onDismissPermanentDelete() }
                    ) {
                        Text("Cancel", color = TextMuted)
                    }
                }
            )
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (note.isRemote) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LinuxPlusAccent.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "From API",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = LinuxPlusAccent
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LinuxPlusBackground.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Local",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp
                                ),
                                color = TextDark
                            )
                        }
                    }

                    val displayDate = getDisplayDate(note.timestamp, note.createdAt)
                    if (displayDate.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = displayDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "🗑️",
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TrashNoteCard(
    note: Note,
    onRestore: () -> Unit,
    onDeletePermanently: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Gray.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Trashed",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp
                        ),
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = TextDark.copy(alpha = 0.8f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onRestore,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("↺ Restore", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDeletePermanently,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LinuxPlusAccent,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Delete Permanently", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
