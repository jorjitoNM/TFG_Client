package com.example.client.ui.normalNoteScreen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.formatDateTime

@Composable
fun NoteDetailScreen(
    noteId: Int,
    showSnackbar: (String) -> Unit,
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
            )
        }
    }
}

@Composable
fun NoteDetailContent(
    state: NoteDetailState,

    ) {
    val note = state.note ?: return
    val photoList = List(8) { R.drawable.ic_launcher_background }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with back button and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = note.content ?: "Una breve descripción de esta nota",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rating: X/10 y estrella dorada
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color(0xFFFFD700), // Gold color
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${note.rating}/10",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Si es evento, mostrar fechas
        if (note.type == NoteType.EVENT) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Evento",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Start: ${formatDateTime(note.start ?: "")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                    Text(
                        text = "End: ${formatDateTime(note.end ?: "")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Fecha de creación
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatDateTime(note.created.toString()),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Icono de privacidad según el tipo
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
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Only for you",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
                NotePrivacy.PUBLIC -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Pública",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Public",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Photos section
        Text(
            text = "Photos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LazyRow for photos (mock)
        // Photos section en grid de 2 columnas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp) // Altura fija para la galería
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(photoList.size) { idx ->
                    Image(
                        painter = painterResource(id = photoList[idx]),
                        contentDescription = "Photo $idx",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
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
                privacy = NotePrivacy.PUBLIC,
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