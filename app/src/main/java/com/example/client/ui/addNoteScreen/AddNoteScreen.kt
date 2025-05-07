package com.example.client.ui.addNoteScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.Note
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
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
    note: NoteDTO?,
    onEdit: (note: NoteDTO) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var privacy by remember { mutableStateOf(note?.privacy ?: NotePrivacy.FOLLOWERS) }
    var rating by remember { mutableStateOf(note?.rating ?: 5) }
    var latitude by remember { mutableStateOf(note?.latitude ?: 0.0) }
    var longitude by remember { mutableStateOf(note?.longitude ?: 0.0) }
    var type by remember { mutableStateOf(note?.type ?: NoteType.CLASSIC) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = title,
            onValueChange = {
                title = it
                note?.let { onEdit(it.copy(title = title)) }
            },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = {
                content = it
                note?.let { onEdit(it.copy(content = content)) }
            },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenuField(
            label = "Privacidad",
            options = NotePrivacy.values().toList(),
            selectedOption = privacy,
            onOptionSelected = {
                privacy = it
                note?.let { onEdit(it.copy(privacy = privacy)) }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = rating.toString(),
            onValueChange = {
                rating = it.toIntOrNull() ?: rating
                note?.let { onEdit(it.copy(rating = rating)) }
            },
            label = { Text("Calificación") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = latitude.toString(),
            onValueChange = {
                latitude = it.toDoubleOrNull() ?: latitude
                note?.let { onEdit(it.copy(latitude = latitude)) }
            },
            label = { Text("Latitud") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = longitude.toString(),
            onValueChange = {
                longitude = it.toDoubleOrNull() ?: longitude
                note?.let { onEdit(it.copy(longitude = longitude)) }
            },
            label = { Text("Longitud") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenuField(
            label = "Tipo",
            options = NoteType.values().toList(),
            selectedOption = type,
            onOptionSelected = {
                type = it
                note?.let { onEdit(it.copy(type = type)) }
            }
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

@Composable
fun <T> DropdownMenuField(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextField(
            value = selectedOption.toString(),
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(option.toString()) }
                )
            }
        }
    }
}