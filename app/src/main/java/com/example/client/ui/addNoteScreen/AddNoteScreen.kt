package com.example.client.ui.addNoteScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.domain.model.note.Note
import com.example.client.ui.common.UiEvent
@Composable
fun AddNoteScreen(
    addViewModel: AddViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    showSnackbar: (String) -> Unit = {}
) {
    val uiState = addViewModel.uiState.collectAsStateWithLifecycle().value

    // Cargar la nota inicial
    LaunchedEffect(Unit) {
        addViewModel.handleEvent(AddEvent.LoadNote)
    }

    // Mostrar contenido o indicador de carga
    if (!uiState.isLoading) {
        AddNoteContent(
            note = uiState.note,
            onEdit = { note -> addViewModel.handleEvent(AddEvent.EditNote(note)) },
            onSave = { addViewModel.handleEvent(AddEvent.addNote) },
            onNavigateBack = onNavigateBack
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator()
        }
    }

    // Manejar eventos de UI
    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> showSnackbar(it.message)
                is UiEvent.Navigate -> onNavigateBack()
            }
            addViewModel.handleEvent(AddEvent.UiEventDone)
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