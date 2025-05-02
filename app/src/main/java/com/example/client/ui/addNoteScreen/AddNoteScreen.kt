package com.example.client.ui.addNoteScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.client.domain.model.note.Note
import com.example.client.ui.common.UiEvent
import com.example.client.ui.normalNoteScreen.detail.NoteDetailEvent

@Composable
fun AddNoteScreen(
    addViewModel: AddViewModel = hiltViewModel(),
    navController: NavController,
    showSnackbar: (String) -> Unit = {}
) {
    val uiState = addViewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    addViewModel.handleEvent(AddEvent.UiEventDone)
                }
                else -> {}
            }
        }
    }


    if (!uiState.isLoading) {
        AddNoteContent(
            note = uiState.note,
            onEdit = { note -> addViewModel.handleEvent(AddEvent.editNote(note)) },
            onSave = { addViewModel.handleEvent(AddEvent.addNote) },
            onNavigateBack = { navController.popBackStack() }
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun AddNoteContent(
    note: Note,
    onEdit: (note: Note) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(note.tittle) }
    var content by remember { mutableStateOf(note.content) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = {
                title = it
                onEdit(note.copy(tittle = it))
            },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = {
                content = it
                onEdit(note.copy(content = it))
            },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onSave) {
                Text("Guardar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onNavigateBack) {
                Text("Cancelar")
            }
        }
    }
}