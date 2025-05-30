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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
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
import androidx.compose.material3.MaterialTheme
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
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        addNoteViewModel.handleEvent(AddNoteEvents.CheckLocationPermission)
        if (!isGranted) {
            showSnackbar("Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        addNoteViewModel.handleEvent(AddNoteEvents.CheckLocationPermission)
    }

    LaunchedEffect(uiState.hasLocationPermission) {
        if (!uiState.hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Note", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Check, contentDescription = "Save note")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            AddNoteContent(
                modifier = Modifier.padding(padding),
                note = uiState.note,
                onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
                onRequestLocation = { addNoteViewModel.handleEvent(AddNoteEvents.GetCurrentLocation) },
                onSave = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) },
                onExit = onNavigateBack
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
    onSave: () -> Unit,
    onExit: () -> Unit
) {
    var localNote by remember { mutableStateOf(note) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = localNote.title,
                    onValueChange = {
                        localNote = localNote.copy(title = it)
                        onEdit(localNote)
                    },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNote.content ?: "",
                    onValueChange = {
                        localNote = localNote.copy(content = it)
                        onEdit(localNote)
                    },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DropdownMenuField(
                        label = "Privacy",
                        options = NotePrivacy.entries,
                        selectedOption = localNote.privacy,
                        onOptionSelected = {
                            localNote = localNote.copy(privacy = it)
                            onEdit(localNote)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    DropdownMenuField(
                        label = "Type",
                        options = NoteType.entries,
                        selectedOption = localNote.type,
                        onOptionSelected = {
                            localNote = localNote.copy(type = it)
                            onEdit(localNote)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Column {
                    Text("Rating: ${localNote.rating}", style = MaterialTheme.typography.labelLarge)
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
                            label = { Text("Latitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = localNote.longitude.toString(),
                            onValueChange = {
                                localNote = localNote.copy(longitude = it.toDouble())
                                onEdit(localNote)
                            },
                            label = { Text("Longitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    IconButton(
                        onClick = onRequestLocation,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = "Use current location")
                    }
                }

                if (localNote.type == NoteType.EVENT) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = localNote.start ?: "",
                            onValueChange = {
                                localNote = localNote.copy(start = it)
                                onEdit(localNote)
                            },
                            label = { Text("Start") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.Check, null) },
                            readOnly = true
                        )
                        OutlinedTextField(
                            value = localNote.end ?: "",
                            onValueChange = {
                                localNote = localNote.copy(end = it)
                                onEdit(localNote)
                            },
                            label = { Text("End") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = { Icon(Icons.Default.AddCircle, null) },
                            readOnly = true
                        )
                    }
                }
            }
        }

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Save Note", style = MaterialTheme.typography.labelLarge)
        }
        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Salir", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun <T> DropdownMenuField(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedOption.toString(),
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, null)
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
}

@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun AddNoteScreenPreview() {
    MaterialTheme {
        AddNoteContent(
            note = NoteDTO(
                id = 1,
                title = "Sample Title",
                content = "Sample content for this note",
                privacy = NotePrivacy.PUBLIC,
                rating = 3,
                ownerUsername = "user",
                likes = 12,
                created = "2025-05-26",
                latitude = 40.4168,
                longitude = -3.7038,
                type = NoteType.EVENT,
                start = "313212",
                end = "3232324",
            ),
            onEdit = {},
            onRequestLocation = {},
            onSave = {},
            onExit = {}
        )
    }
}