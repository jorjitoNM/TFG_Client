package com.example.client.ui.addNoteScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent

@Composable
fun AddNoteScreen(
    addNoteViewModel: AddNoteViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
) {
    val uiState by addNoteViewModel.uiState.collectAsStateWithLifecycle()

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        addNoteViewModel.handleEvent(AddNoteEvents.CheckLocationPermission)
        if (isGranted) {
            addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation)
        } else {
            showSnackbar("Permiso de ubicación denegado")
        }
    }

    LaunchedEffect(Unit) {
        addNoteViewModel.handleEvent(AddNoteEvents.CheckLocationPermission)


        val requestPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation)
            } else {
                showSnackbar("Permiso de ubicación denegado")
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!uiState.hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation)
        }
    }

    LaunchedEffect(uiState.uiEvent) {
        uiState.uiEvent?.let {

            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    addNoteViewModel.handleEvent(AddNoteEvents.UiNoteEventsDone)
                }

                is UiEvent.PopBackStack -> onNavigateBack()
            }
        }
    }

    if (!uiState.isLoading) {
        AddNoteContent(
            note = uiState.note,
            onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
            onSave = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) },
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
}

@Composable
fun AddNoteContent(
    note: NoteDTO,
    onEdit: (note: NoteDTO) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val noteState = remember { mutableStateOf(note) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Añadir Nota",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        TextField(
            value = noteState.value.title,
            onValueChange = {
                noteState.value = noteState.value.copy(title = it)
                onEdit(noteState.value)
            },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        noteState.value.content?.let {
            TextField(
                value = it,
                onValueChange = {
                    noteState.value = noteState.value.copy(content = it)
                    onEdit(noteState.value)
                },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuField(
            label = "Privacidad",
            options = NotePrivacy.values().toList(),
            selectedOption = noteState.value.privacy,
            onOptionSelected = {
                noteState.value = noteState.value.copy(privacy = it)
                onEdit(noteState.value)
            }
        )
        DropdownMenuField(
            label = "Tipo",
            options = NoteType.entries,
            selectedOption = noteState.value.type,
            onOptionSelected = {
                noteState.value = noteState.value.copy(type = it)
                onEdit(noteState.value)
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

@Preview
@Composable
fun AddNoteScreenPreview() {
    AddNoteContent(NoteDTO(
        1,
        "asd",
        "asd",
        NotePrivacy.FOLLOWERS,
        4,
        "juan",
        50,
        "ayer",
        1.6,
        5.6,
        NoteType.FOOD,
        null,
        null
    ),
        {}, {}, {})
}