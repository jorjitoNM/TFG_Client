package com.example.client.ui.normalNoteScreen.detail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.normalNoteScreen.list.formatDateTime

@Composable
fun NoteDetailScreen(
    noteId: Int,
    showSnackbar: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(NoteDetailEvent.GetNote(noteId))
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
                onTitleChange = { viewModel.handleEvent(NoteDetailEvent.UpdateEditedTitle(it)) },
                onContentChange = { viewModel.handleEvent(NoteDetailEvent.UpdateEditedContent(it)) },
                onPrivacyChange = { viewModel.handleEvent(NoteDetailEvent.UpdateEditedPrivacy(it)) },
                onRatingChange = { viewModel.handleEvent(NoteDetailEvent.RateNote(it)) },
                onEditClick = { viewModel.handleEvent(NoteDetailEvent.ToggleEditMode) },
                onSaveClick = { viewModel.handleEvent(NoteDetailEvent.UpdateNote) },
                onCancelClick = { viewModel.handleEvent(NoteDetailEvent.ToggleEditMode) },
                onBackClick = onNavigateBack,
                onDeleteClick = { /* Implement if needed */ },
                onLikeClick = { viewModel.handleEvent(NoteDetailEvent.LikeNote(noteId))}
            )
        }
    }
}

@Composable
fun NoteDetailContent(
    state: NoteDetailState,
    onTitleChange: (String) -> Unit = {},
    onContentChange: (String) -> Unit = {},
    onPrivacyChange: (NotePrivacy) -> Unit = {},
    onRatingChange: (Int) -> Unit = {},
    onEditClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onLikeClick : (noteId : Int) -> Unit = {},
) {
    val note = state.note ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                if (state.isEditing) {
                    OutlinedTextField(
                        value = state.editedTitle,
                        onValueChange = onTitleChange,
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${note.latitude}, ${note.longitude}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Content
                if (state.isEditing) {
                    OutlinedTextField(
                        value = state.editedContent,
                        onValueChange = onContentChange,
                        label = { Text("Content") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Privacy dropdown
                    Text(
                        text = "Privacy",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    PrivacyDropdown(
                        selectedPrivacy = state.editedPrivacy,
                        onPrivacySelected = onPrivacyChange
                    )
                } else {
                    Text(
                        text = note.content ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Event details if it's an event
                    if (note.type == NoteType.EVENT) {
                        EventDetails(note)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "By: ${note.ownerUsername ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Created: ${formatDateTime(note.created.toString())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow( modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.3f)
                        .padding(horizontal = 16.dp)
                    ) {
                        itemsIndexed(note.photos) { index, url ->
                            if (index == note.photos.size)
                                PlusImageButton()
                            else
                                ImageItem(url)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating
                Text(
                    text = "Rating",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                RatingBar(
                    currentRating = note.rating,
                    onRatingChanged = onRatingChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onBackClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Back")
                    }

                    if (state.isEditing) {
                        Row {
                            Button(
                                onClick = onCancelClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = onSaveClick,
                                enabled = !state.isUpdating
                            ) {
                                if (state.isUpdating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text("Save")
                                }
                            }
                        }
                    } else {
                        Row {
                            Button(
                                onClick = onDeleteClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = MaterialTheme.colorScheme.onError
                                ),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }

                            Button(onClick = onEditClick) {
                                Text("Edit")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlusImageButton() {
    Box(modifier = Modifier
        .padding(4.dp)
        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ImageItem(url: String) {
    AsyncImage(
        model = url,
        contentDescription = "Imagine this is an epic photo",
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
    )
}

@Composable
fun EventDetails(note: NoteDTO) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Event",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Event Details",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Start",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDateTime(note.start ?: ""),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column {
                    Text(
                        text = "End",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = formatDateTime(note.end ?: ""),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    currentRating: Int,
    onRatingChanged: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= currentRating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Star $i",
                    tint = if (i <= currentRating) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRatingChanged(i) }
                        .padding(end = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$currentRating/5",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PrivacyDropdown(
    selectedPrivacy: NotePrivacy,
    onPrivacySelected: (NotePrivacy) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedPrivacy.name,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            NotePrivacy.entries.forEach { privacy ->
                DropdownMenuItem(
                    text = { Text(privacy.name) },
                    onClick = {
                        onPrivacySelected(privacy)
                        expanded = false
                    }
                )
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
                privacy = NotePrivacy.FOLLOWERS,
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
            NoteDetailContent(state = state)
        }
    }
}