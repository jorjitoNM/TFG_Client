package com.example.client.ui.addNoteScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun AddNoteScreen(
    addNoteViewModel: AddNoteViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
) {
    val uiState by addNoteViewModel.uiState.collectAsStateWithLifecycle()

    // Permission handling
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

    // One-shot UI events
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


        if (uiState.isLoading) {
            Box(
                Modifier
                    .fillMaxSize(),
                Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AddNoteContent(
                note = uiState.note,
                onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
                onAddNote = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) }
            )
        }

}

@Composable
private fun AddNoteContent(
    modifier: Modifier = Modifier,
    note: NoteDTO,
    onEdit: (NoteDTO) -> Unit,
    onAddNote: () -> Unit
) {
    var localNote by remember { mutableStateOf(note) }

    Timber.d("lat: ${localNote.latitude}, lon: ${localNote.longitude}")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Privacy Tabs
        PrivacyTabs(
            selectedPrivacy = localNote.privacy,
            onPrivacySelected = { privacy ->
                localNote = localNote.copy(privacy = privacy)
                onEdit(localNote)
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Name Field
            Column {
                Text(
                    text = "Nombre",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = localNote.title,
                    onValueChange = {
                        localNote = localNote.copy(title = it)
                        onEdit(localNote)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Blue
                    ),
                    singleLine = true
                )
            }

            // Description Field
            Column {
                Text(
                    text = "Descripción",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = localNote.content ?: "",
                    onValueChange = {
                        localNote = localNote.copy(content = it)
                        onEdit(localNote)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.Blue
                    ),
                    minLines = 3
                )
            }

            // Rating Section
            Column {
                Text(
                    text = "Valoración",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                StarRating(
                    rating = localNote.rating,
                    onRatingChanged = { rating ->
                        localNote = localNote.copy(rating = rating)
                        onEdit(localNote)
                    }
                )
            }

            // Date Field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fontSize = 16.sp,
                    color = Color.Gray
                )


            }

            // Photos Section
            Column {
                Text(
                    text = "Fotos",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedButton(
                    onClick = { /* Handle photo selection */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Añadir fotos",
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Add Button
            Button(
                onClick = onAddNote,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "AÑADIR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacyTabs(
    selectedPrivacy: NotePrivacy,
    onPrivacySelected: (NotePrivacy) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp)
    ) {
        NotePrivacy.entries.forEach { privacy ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onPrivacySelected(privacy) }
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = privacy.displayName,
                    fontSize = 16.sp,
                    color = if (selectedPrivacy == privacy) Color(0xFF4285F4) else Color.Gray,
                    fontWeight = if (selectedPrivacy == privacy) FontWeight.Medium else FontWeight.Normal
                )

                if (selectedPrivacy == privacy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(Color(0xFF4285F4))
                    )
                }
            }
        }
    }
}

@Composable
private fun StarRating(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    maxRating: Int = 5
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxRating) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.LightGray,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onRatingChanged(index + 1) }
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
        onAddNote = {}
    )
}