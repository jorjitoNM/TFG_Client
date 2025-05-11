package com.example.client.ui.normalNoteScreen.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.ui.common.UiEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.noteScreen.list.NoteListViewModel


@Composable
fun NoteListScreen(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(NoteListEvent.GetNotes)
    }

    LaunchedEffect(state.aviso) {
        state.aviso?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    viewModel.handleEvent(NoteListEvent.AvisoVisto)
                }
                is UiEvent.PopBackStack -> {
                    onNavigateToDetail(state.selectedNoteId)
                    viewModel.handleEvent(NoteListEvent.AvisoVisto)
                }
            }
        }
    }
    val filteredNotes = if (searchQuery.isBlank()) {
        state.notes
    } else {
        state.notes.filter { note ->
            note.title.contains(searchQuery, ignoreCase = true) ||
                    (note.content?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        NoteListContent(
            notes = filteredNotes,
            onNoteClick = { noteId ->
                viewModel.handleEvent(NoteListEvent.SelectedNote(noteId))
            },
            onFavClick = { noteId ->
                viewModel.handleEvent(NoteListEvent.FavNote(noteId))
            }
        )
    }
}

@Composable
private fun NoteListContent(
    notes: List<NoteDTO>,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit
) {
    LazyColumn {
        items(notes) { note ->
            NoteItem(
                note = note,
                onClick = { onNoteClick(note.id) },
                onFavClick = { onFavClick(note.id) }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: NoteDTO,
    onClick: () -> Unit,
    onFavClick: (Int) -> Unit
) {
    val goldColor = Color(0xFFFFD700)
    val pinkColor = Color(0xFFFF4081)
    val cardBackground = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            NoteHeader(note, textColor)
            Spacer(modifier = Modifier.height(8.dp))
            NoteContent(note, textColor)
            Spacer(modifier = Modifier.height(12.dp))
            NoteFooter(note, textColor)
            NoteActions(note, goldColor, pinkColor, textColor, onFavClick)
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar notas...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
private fun NoteHeader(note: NoteDTO, textColor: Color) {
    Text(
        text = note.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = textColor
    )
}

@Composable
private fun NoteContent(note: NoteDTO, textColor: Color) {
    Text(
        text = note.content ?: "",
        style = MaterialTheme.typography.bodyMedium,
        color = textColor.copy(alpha = 0.8f),
        maxLines = 2
    )
}

@Composable
private fun NoteFooter(note: NoteDTO, textColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "By: ${note.ownerUsername ?: "Unknown"}",
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.6f)
        )
        RatingIndicator(note.rating)
    }

    if (note.type == NoteType.EVENT) {
        EventDates(note)
    }
}

@Composable
private fun RatingIndicator(rating: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = when {
                    rating >= 8 -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                    rating >= 5 -> Color(0xFFFFC107).copy(alpha = 0.2f)
                    else -> Color(0xFFF44336).copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "★ $rating/10",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                rating >= 8 -> Color(0xFF4CAF50)
                rating >= 5 -> Color(0xFFFFC107)
                else -> Color(0xFFF44336)
            },
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EventDates(note: NoteDTO) {
    Column {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "EVENT",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Start: ${note.start?.formatDateTime()}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "End: ${note.end?.formatDateTime()}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun NoteActions(
    note: NoteDTO,
    goldColor: Color,
    pinkColor: Color,
    textColor: Color,
    onFavClick: (Int) -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = {
                isFavorite = !isFavorite
                onFavClick(note.id)
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = "Favorito",
                tint = if (isFavorite) goldColor else textColor.copy(alpha = 0.4f),
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        IconButton(
            onClick = { isLiked = !isLiked },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                contentDescription = "Me gusta",
                tint = if (isLiked) pinkColor else textColor.copy(alpha = 0.4f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// Función de extensión para formatear fechas
fun String.formatDateTime(): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(this, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        this
    }
}

@Preview
@Composable
fun NoteItemPreview() {
    NoteItem(
        note = NoteDTO(
            id = 1,
            title = "Título de ejemplo",
            content = "Contenido de la nota con texto más largo",
            rating = 8,
            type = NoteType.EVENT,
            start = "2025-05-11T10:00:00",
            end = "2025-05-11T12:00:00"
        ),
        onClick = {},
        onFavClick = {}
    )
}

@Preview
@Composable
fun NoteListPreview() {
    NoteListContent(
        notes = listOf(
            NoteDTO(
                id = 1,
                title = "Nota importante",
                content = "Contenido de ejemplo",
                rating = 9
            ),
            NoteDTO(
                id = 2,
                title = "Evento semanal",
                content = "Reunión de equipo",
                rating = 7,
                type = NoteType.EVENT,
                start = "2025-05-11T09:00:00",
                end = "2025-05-11T10:00:00"
            )
        ),
        onNoteClick = {},
        onFavClick = {}
    )
}






