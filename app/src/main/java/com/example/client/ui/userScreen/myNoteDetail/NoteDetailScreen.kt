package com.example.client.ui.userScreen.myNoteDetail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.ColorfulLinearRatingBar
import com.example.client.ui.common.composables.formatDateTime
import com.example.client.ui.normalNoteScreen.detail.AddImageButton

@Composable
fun NoteDetailScreen(
    noteId: Int,
    showSnackbar: (String) -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = isSystemInDarkTheme()

    // Obtener nota y cargar imágenes
    LaunchedEffect(noteId) {
        viewModel.handleEvent(NoteDetailEvent.GetNote(noteId))
    }
    // Cargar imágenes cuando la nota esté lista
    LaunchedEffect(state.note?.id) {
        state.note?.id?.let { viewModel.loadNoteImages(it) }
    }
    LaunchedEffect(state.aviso) {
        state.aviso?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    viewModel.handleEvent(NoteDetailEvent.AvisoVisto)
                }
                else -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            NoteDetailContent(
                state = state,
                onEvent = viewModel::handleEvent,
                isDarkMode = isDarkMode
            )
        }
    }
}


@Composable
fun NoteDetailContent(
    state: NoteDetailState,
    onEvent: (NoteDetailEvent) -> Unit,
    isDarkMode: Boolean
) {
    val note = state.note ?: return

    // Colores dependientes del modo
    val backgroundColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black
    val secondaryTextColor = if (isDarkMode) Color(0xFFB0B0B0) else Color.DarkGray
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color.LightGray
    val cardColor = if (isDarkMode) Color(0xFF2D313A) else Color(0xFFF5F5F5)
    val iconTint = if (isDarkMode) Color(0xFFE0E0E0) else Color.Black
    val iconDeleteBg = if (isDarkMode) Color(0xFF23272F) else Color.White
    val iconDeleteTint = Color.Red
    val starColor = Color(0xFFFFD700)
    val outlinedFieldBg = if (isDarkMode) Color(0xFF23272F) else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with back button, title and edit button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isEditing) {
                OutlinedTextField(
                    value = state.editedTitle,
                    onValueChange = { onEvent(NoteDetailEvent.UpdateEditedTitle(it)) },
                    label = { Text("Title") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = dividerColor,
                        unfocusedBorderColor = dividerColor,
                        focusedContainerColor = outlinedFieldBg,
                        unfocusedContainerColor = outlinedFieldBg,
                        disabledContainerColor = outlinedFieldBg,
                        cursorColor = textColor
                    ),
                    textStyle = LocalTextStyle.current.copy(color = textColor)
                )
            } else {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Edit/Save/Cancel buttons
            if (state.isEditing) {
                Row {
                    IconButton(onClick = { onEvent(NoteDetailEvent.UpdateNote) }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = Color.Green
                        )
                    }
                    IconButton(onClick = { onEvent(NoteDetailEvent.ToggleEditMode) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = Color.Red
                        )
                    }
                }
            } else {
                IconButton(onClick = { onEvent(NoteDetailEvent.ToggleEditMode) }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = iconTint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        if (state.isEditing) {
            OutlinedTextField(
                value = state.editedContent,
                onValueChange = { onEvent(NoteDetailEvent.UpdateEditedContent(it)) },
                label = { Text("Content", color = textColor) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = dividerColor,
                    unfocusedBorderColor = dividerColor,
                    focusedContainerColor = outlinedFieldBg,
                    unfocusedContainerColor = outlinedFieldBg,
                    disabledContainerColor = outlinedFieldBg,
                    cursorColor = textColor
                ),
                textStyle = LocalTextStyle.current.copy(color = textColor)
            )
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            if (note.content != null) {
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = secondaryTextColor
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rating section
        if (state.isEditing) {
            Text(
                text = "Rating",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            ColorfulLinearRatingBar(
                rating = note.rating,
                onRatingChanged = { newRating -> onEvent(NoteDetailEvent.RateNote(newRating)) },
                maxRating = 10,
                isDarkMode = isDarkMode,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Rating: X/10 y estrella dorada
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rating",
                    tint = starColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${note.rating}/10",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy section
        if (state.isEditing) {
            Text(
                text = "Privacy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                NotePrivacy.values().forEach { privacy ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEvent(NoteDetailEvent.UpdateEditedPrivacy(privacy)) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = state.editedPrivacy == privacy,
                            onClick = { onEvent(NoteDetailEvent.UpdateEditedPrivacy(privacy)) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF4285F4),
                                unselectedColor = dividerColor
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        when (privacy) {
                            NotePrivacy.PRIVATE -> {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Private",
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Only for you", color = textColor)
                            }

                            NotePrivacy.PUBLIC -> {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Public",
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Public", color = textColor)
                            }
                        }
                    }
                }
            }
        } else {
            // Icono de privacidad según el tipo, con texto al costado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                when (note.privacy) {
                    NotePrivacy.PRIVATE -> {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Privada",
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Only for you",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }

                    NotePrivacy.PUBLIC -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Pública",
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Public",
                            style = MaterialTheme.typography.bodyLarge,
                            color = textColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fechas de evento
        if (note.type == NoteType.EVENT && !state.isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Evento",
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Start: ${formatDateTime(note.start ?: "")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                    Text(
                        text = "End: ${formatDateTime(note.end ?: "")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!state.isEditing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date",
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDateTime(note.created.toString()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = dividerColor
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
        ) {
            val photos = note.photos
            if (state.isImagesLoading) {
                // Loader SOLO en la galería
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = textColor)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(photos.size) { idx ->
                        Box(modifier = Modifier.aspectRatio(1f)) {
                            AsyncImage(
                                model = photos[idx],
                                contentDescription = "Photo $idx",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardColor)
                            )
                            // Solo muestra el botón de eliminar si está en modo edición
                            if (state.isEditing) {
                                IconButton(
                                    onClick = { onEvent(NoteDetailEvent.DeleteImage(photos[idx])) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(
                                            iconDeleteBg.copy(alpha = 0.8f),
                                            shape = CircleShape
                                        )
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete photo",
                                        tint = iconDeleteTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    // Solo muestra el botón de agregar si está en modo edición y hay menos de 4 fotos
                    if (state.isEditing && photos.size < 8) {
                        item {
                            AddImageButton(
                                onLoadImages = { uris -> onEvent(NoteDetailEvent.SaveNoteImages(uris)) },
                            )
                        }
                    }
                }
            }
        }
    }
}






class NoteDetailStateProvider : PreviewParameterProvider<NoteDetailState> {
    override val values = sequenceOf(
        // Loading state
        NoteDetailState(isLoading = true),

        // Viewing state (classic note)
        NoteDetailState(
            note = NoteDTO(
                id = 1,
                title = "My First Note",
                content = "This is the content of my first note. It's a classic note without any special features.",
                privacy = NotePrivacy.PUBLIC,
                rating = 3,
                ownerUsername = "user123",
                created = "2023-04-15T10:30:00",
                latitude = 40.4168,
                longitude = -3.7038,
                type = NoteType.CLASSIC
            ),
            isLoading = false
        ),

        // Viewing state (event note)
        NoteDetailState(
            note = NoteDTO(
                id = 2,
                title = "Team Meeting",
                content = "Discuss project roadmap and assign tasks for the next sprint.",
                privacy = NotePrivacy.PUBLIC,
                rating = 4,
                ownerUsername = "manager",
                created = "2023-04-15T10:30:00",
                latitude = 40.4168,
                longitude = -3.7038,
                type = NoteType.EVENT,
                start = "2023-04-15T10:30:00",
                end = "2023-04-15T11:30:00"
            ),
            isLoading = false
        ),

        // Editing state
        NoteDetailState(
            note = NoteDTO(
                id = 3,
                title = "Shopping List",
                content = "Milk, eggs, bread, cheese",
                privacy = NotePrivacy.PRIVATE,
                rating = 5,
                ownerUsername = "user123",
                created = "2023-04-15T10:30:00",
                latitude = 40.4168,
                longitude = -3.7038,
                type = NoteType.CLASSIC
            ),
            isLoading = false,
            isEditing = true,
            editedTitle = "Shopping List",
            editedContent = "Milk, eggs, bread, cheese",
            editedPrivacy = NotePrivacy.PRIVATE
        ),

        // Updating state
        NoteDetailState(
            note = NoteDTO(
                id = 4,
                title = "Workout Plan",
                content = "Monday: Chest and Triceps\nWednesday: Back and Biceps\nFriday: Legs and Shoulders",
                privacy = NotePrivacy.PUBLIC,
                rating = 4,
                ownerUsername = "user123",
                created = "2023-04-15T10:30:00",
                latitude = 40.4168,
                longitude = -3.7038,
                type = NoteType.CLASSIC
            ),
            isLoading = false,
            isEditing = true,
            isUpdating = true,
            editedTitle = "Workout Plan",
            editedContent = "Monday: Chest and Triceps\nWednesday: Back and Biceps\nFriday: Legs and Shoulders",
            editedPrivacy = NotePrivacy.PUBLIC
        )
    )
}

// Preview composable
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun NoteDetailScreenPreview(@PreviewParameter(NoteDetailStateProvider::class) state: NoteDetailState) {
    MaterialTheme {
        Surface {
            NoteDetailContent(state = state, onEvent = {}, isDarkMode = true )
        }
    }
}