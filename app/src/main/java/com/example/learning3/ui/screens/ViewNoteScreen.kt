package com.example.learning3.ui.screens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learning3.composables.Dialog
import com.example.learning3.data.Note
import com.example.learning3.events.NoteEvent
import com.example.learning3.ui.state.NoteState
import com.example.learning3.navigation.Screen
import com.example.learning3.utilities.UtilityFunctions.exportNoteDialogBody
import com.example.learning3.utilities.UtilityFunctions.exportNoteDialogSuccessMsg
import com.example.learning3.utilities.UtilityFunctions.exportNoteDialogTitle
import com.example.learning3.utilities.UtilityFunctions.formatDateAndTime

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewNoteScreen(
    navController: NavController,
    selectedNoteId: Long,
    state: NoteState,
    onEvent: (NoteEvent) -> Unit
) {
    val note: Note? = state.notes.find { note ->
        note.id == selectedNoteId
    }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (showDeleteDialog) {
        Dialog(
            titleText = "Confirm deletion?",
            bodyText = "The note will be deleted permanently",
            confirmButtonText = "Delete",
            onDismissRequest = { showDeleteDialog = false },
            onConfirmButtonClick = {
                onEvent(NoteEvent.DeleteNote(note!!))
                navController.navigate(Screen.Notes.route) {
                    popUpTo(Screen.Notes.route) {
                        inclusive = true
                    }
                }
            },
            onDismissButtonClick = {
                showDeleteDialog = false
            }
        )
    }

    if (showExportDialog && note != null) {
        val selectedNotes = listOf(note)
        Dialog(
            titleText = exportNoteDialogTitle(selectedNotes),
            bodyText = exportNoteDialogBody(selectedNotes),
            confirmButtonText = "Export",
            onDismissRequest = { showExportDialog = false },
            onDismissButtonClick = { showExportDialog = false },
            onConfirmButtonClick = {
                expanded = false
                onEvent(NoteEvent.ExportNote(note))
                Toast.makeText(
                    context,
                    exportNoteDialogSuccessMsg(selectedNotes),
                    Toast.LENGTH_SHORT
                ).show()
                showExportDialog = false
            })
    }
    Scaffold(
        containerColor = MaterialTheme.colorScheme.inverseOnSurface,
        contentColor = MaterialTheme.colorScheme.inverseSurface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Last modified on ${(note?.lastModified ?: 0).formatDateAndTime()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                        )
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.inverseSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.inverseSurface,
                    titleContentColor = MaterialTheme.colorScheme.inverseSurface
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            expanded = true
                        }
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(
                            extraSmall = RoundedCornerShape(16.dp)
                        ),
                    ) {
                        DropdownMenu(
                            expanded = expanded,
                            offset = DpOffset(10.dp, 0.dp),
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = {
                                    expanded = false
                                    note?.let {
                                        onEvent(NoteEvent.StartEditing(it))
                                    }
                                    navController.navigate("${Screen.EditNote.route}/${selectedNoteId}")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export") },
                                onClick = {
                                    expanded = false
                                    showExportDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    expanded = false
                                    showDeleteDialog = showDeleteDialog.not()
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(
                    horizontal = 16.dp,
                )
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = note?.title ?: "",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                ),
            )
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )
            Text(
                text = note?.content ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                )
            )
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )
        }
    }
}