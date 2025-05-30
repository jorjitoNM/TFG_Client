package com.example.client.ui.addNoteScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(
    addNoteViewModel: AddNoteViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
) {
    val uiState by addNoteViewModel.uiState.collectAsStateWithLifecycle()

    /* --- PERMISSION HANDLING ------------------------------------------------ */
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
    }

    LaunchedEffect(uiState.hasLocationPermission) {
        if (!uiState.hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation)
        }
    }

    /* --- ONE-SHOT UI EVENTS -------------------------------------------------- */
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

    /* --- UI ------------------------------------------------------------------ */
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Añadir nota") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote)
            }) {
                Icon(Icons.Default.Check, contentDescription = "Guardar nota")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AddNoteContent(
                modifier = Modifier.padding(padding),
                note = uiState.note,
                onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
                onRequestLocation = { addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation) }
            )
        }
    }
}

@Composable
private fun AddNoteContent(
    modifier: Modifier = Modifier,
    note: NoteDTO,
    onEdit: (NoteDTO) -> Unit,
    onRequestLocation: () -> Unit,
) {
    var localNote by remember { mutableStateOf(note) }

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            /* --- TITLE ------------------------------------------------------- */
            OutlinedTextField(
                value = localNote.title,
                onValueChange = {
                    localNote = localNote.copy(title = it)
                    onEdit(localNote)
                },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) }
            )

            /* --- CONTENT ----------------------------------------------------- */
            OutlinedTextField(
                value = localNote.content ?: "",
                onValueChange = {
                    localNote = localNote.copy(content = it)
                    onEdit(localNote)
                },
                label = { Text("Contenido") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                minLines = 4
            )

            /* --- PRIVACY ----------------------------------------------------- */
            DropdownMenuField(
                label = "Privacidad",
                options = NotePrivacy.entries,
                selectedOption = localNote.privacy,
                onOptionSelected = {
                    localNote = localNote.copy(privacy = it)
                    onEdit(localNote)
                }
            )

            /* --- TYPE -------------------------------------------------------- */
            DropdownMenuField(
                label = "Tipo",
                options = NoteType.entries,
                selectedOption = localNote.type,
                onOptionSelected = {
                    localNote = localNote.copy(type = it)
                    onEdit(localNote)
                }
            )

            /* --- RATING ------------------------------------------------------ */
            Column {
                Text("Valoración: ${localNote.rating}")
                Slider(
                    value = localNote.rating.toFloat(),
                    onValueChange = {
                        localNote = localNote.copy(rating = it.toInt())
                        onEdit(localNote)
                    },
                    valueRange = 0f..5f,
                    steps = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            /* --- LOCATION ---------------------------------------------------- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = localNote.latitude.toString(),
                        onValueChange = {
                            localNote = localNote.copy(latitude = it.toDouble())
                            onEdit(localNote)
                        },
                        label = { Text("Latitud") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = localNote.longitude.toString(),
                        onValueChange = {
                            localNote = localNote.copy(longitude = it.toDouble())
                            onEdit(localNote)
                        },
                        label = { Text("Longitud") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = onRequestLocation) {
                    Icon(Icons.Default.AccountBox, contentDescription = "Usar ubicación actual")
                }
            }

            /* --- EVENT FIELDS ------------------------------------------------- */
            if (localNote.type == NoteType.EVENT) {
                OutlinedTextField(
                    value = localNote.start ?: "",
                    onValueChange = {
                        localNote = localNote.copy(start = it)
                        onEdit(localNote)
                    },
                    label = { Text("Inicio") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                    readOnly = true,
                    /* TODO: sustituir por Date/Time picker */
                )
                OutlinedTextField(
                    value = localNote.end ?: "",
                    onValueChange = {
                        localNote = localNote.copy(end = it)
                        onEdit(localNote)
                    },
                    label = { Text("Fin") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
                    readOnly = true,
                    /* TODO: sustituir por Date/Time picker */
                )
            }
        }
    }
}

@Composable
fun <T> DropdownMenuField(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    OutlinedTextField(
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
        onDismissRequest = { expanded = false },
        modifier = Modifier.fillMaxWidth()
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

/* ---------------------------------------------------------------------------
 * PREVIEW
 * ------------------------------------------------------------------------- */
@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun AddNoteScreenPreview() {
    AddNoteContent(
        note = NoteDTO(
            id = 1,
            title = "Título de ejemplo",
            content = "Contenido de ejemplo",
            privacy = NotePrivacy.PUBLIC,
            rating = 3,
            ownerUsername = "juan",
            likes = 12,
            created = "2025-05-26",
            latitude = 40.4168,
            longitude = -3.7038,
            type = NoteType.EVENT,
            start = "313212",
            end = "3232324",
        ),
        onEdit = {},
        onRequestLocation = {}
    )
}