package com.example.client.ui.addNoteScreen

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.runtime.collectAsState
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
import com.example.client.ui.noteMap.search.SharedLocationViewModel
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Composable
fun AddNoteScreen(
    addNoteViewModel: AddNoteViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit = {},
    onNavigateBack: () -> Unit,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val uiState by addNoteViewModel.uiState.collectAsStateWithLifecycle()
    val sharedLocation by sharedLocationViewModel.selectedLocation.collectAsState()
    val isDarkMode = isSystemInDarkTheme()

    // Actualiza la nota con la ubicación recibida del mapa
    LaunchedEffect(sharedLocation) {
        sharedLocation?.let { (lat, lon) ->
            addNoteViewModel.handleEvent(
                AddNoteEvents.EditNote(
                    uiState.note.copy(latitude = lat, longitude = lon)
                )
            )
            Timber.d("Lat: $lat, Lon: $lon")
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
                .fillMaxSize()
                .background(if (isDarkMode) Color(0xFF23272F) else Color.White),
            Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        AddNoteContent(
            note = uiState.note,
            onEdit = { note -> addNoteViewModel.handleEvent(AddNoteEvents.EditNote(note)) },
            onAddNote = { addNoteViewModel.handleEvent(AddNoteEvents.AddNoteNote) },
            isDarkMode = isDarkMode
        )
    }
}

@Composable
private fun AddNoteContent(
    modifier: Modifier = Modifier,
    note: NoteDTO,
    onEdit: (NoteDTO) -> Unit,
    onAddNote: () -> Unit,
    isDarkMode: Boolean
) {
    var localNote by remember { mutableStateOf(note) }

    val backgroundColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val primaryColor = Color(0xFF4285F4)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Gray
    val inputTextColor = if (isDarkMode) Color.White else Color.Black
    val borderColor = if (isDarkMode) Color(0xFF3A3A3A) else Color.LightGray
    val focusedBorderColor = if (isDarkMode) primaryColor else Color.Blue

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Privacy Tabs
        PrivacyTabs(
            selectedPrivacy = localNote.privacy,
            onPrivacySelected = { privacy ->
                localNote = localNote.copy(privacy = privacy)
                onEdit(localNote)
            },
            isDarkMode = isDarkMode
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
                    text = "Name",
                    fontSize = 16.sp,
                    color = textColor,
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
                        unfocusedBorderColor = borderColor,
                        focusedBorderColor = focusedBorderColor,
                        cursorColor = primaryColor,
                        focusedTextColor = inputTextColor,
                        unfocusedTextColor = inputTextColor
                    ),
                    textStyle = LocalTextStyle.current.copy(color = inputTextColor),
                    singleLine = true
                )
            }

            // Description Field
            Column {
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    color = textColor,
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
                        unfocusedBorderColor = borderColor,
                        focusedBorderColor = focusedBorderColor,
                        cursorColor = primaryColor,
                        focusedTextColor = inputTextColor,
                        unfocusedTextColor = inputTextColor
                    ),
                    textStyle = LocalTextStyle.current.copy(color = inputTextColor),
                    minLines = 3
                )
            }

            // Rating Section
            Column {
                Text(
                    text = "Rating",
                    fontSize = 16.sp,
                    color = textColor,
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
                    tint = textColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    fontSize = 16.sp,
                    color = textColor
                )
            }

            // Photos Section
            Column {
                Text(
                    text = "Photos",
                    fontSize = 16.sp,
                    color = textColor,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedButton(
                    onClick = { /* Handle photo selection */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = textColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Add Photos",
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
                    containerColor = primaryColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "ADD",
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
    onPrivacySelected: (NotePrivacy) -> Unit,
    isDarkMode: Boolean
) {
    val selectedColor = Color(0xFF4285F4)
    val unselectedColor = if (isDarkMode) Color(0xFFBBBBBB) else Color.Gray

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
                    color = if (selectedPrivacy == privacy) selectedColor else unselectedColor,
                    fontWeight = if (selectedPrivacy == privacy) FontWeight.Medium else FontWeight.Normal
                )

                if (selectedPrivacy == privacy) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .background(selectedColor)
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
        onAddNote = {},
        isDarkMode = true
    )
}