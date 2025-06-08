package com.example.client.ui.normalNoteScreen.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NotePrivacy
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.NoteTypeBadge
import com.example.client.ui.common.composables.formatDateTime

@Composable
fun NoteListScreen(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var isFilterExpanded by remember { mutableStateOf(false) }

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

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            NoteListContent(
                notes = state.notes,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onNoteClick = { noteId ->
                    viewModel.handleEvent(NoteListEvent.SelectedNote(noteId))
                },
                onFavClick = { noteId ->
                    val note = state.notes.find { it.id == noteId }
                    note?.let {
                        if (it.saved) {
                            viewModel.handleEvent(NoteListEvent.DelFavNote(noteId))
                        } else {
                            viewModel.handleEvent(NoteListEvent.FavNote(noteId))
                        }
                    }
                },
                onLikeClick = { noteId ->
                    val note = state.notes.find { it.id == noteId }
                    note?.let {
                        if (it.liked) {
                            viewModel.handleEvent(NoteListEvent.DelLikeNote(noteId))
                        } else {
                            viewModel.handleEvent(NoteListEvent.LikeNote(noteId))
                        }
                    }
                },

                isFilterExpanded = isFilterExpanded,
                onFilterExpandToggle = { isFilterExpanded = !isFilterExpanded }
            )

            if (isFilterExpanded) {
                FilterMenuOverlay(
                    onFilterSelected = { filterOption ->
                        viewModel.handleEvent(NoteListEvent.ApplyFilter(filterOption))
                        isFilterExpanded = false
                    },
                    onChronologicalOrderSelected = {
                        viewModel.handleEvent(NoteListEvent.OrderByChronological)
                        isFilterExpanded = false
                    },
                    onNoteTypeFilterSelected = { noteType ->
                        noteType?.let {
                            viewModel.handleEvent(NoteListEvent.OrderByType(it))
                        } ?: viewModel.handleEvent(NoteListEvent.GetNotes)
                        isFilterExpanded = false
                    },
                    onDismiss = { isFilterExpanded = false }
                )
            }
        }
    }
}

@Composable
fun NoteListContent(
    notes: List<NoteDTO>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    isFilterExpanded: Boolean,
    onFilterExpandToggle: () -> Unit,
) {
    val publicNotes = notes.filter { it.privacy == NotePrivacy.PUBLIC }

    val filteredNotes = if (searchQuery.isBlank()) {
        publicNotes
    } else {
        publicNotes.filter { note ->
            note.title.contains(searchQuery, ignoreCase = true) ||
                    (note.content?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        FilterHeader(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            isExpanded = isFilterExpanded,
            onExpandToggle = onFilterExpandToggle
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredNotes) { note ->
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onFavClick = { onFavClick(note.id) },
                    onLikeClick = { onLikeClick(note.id) },
                )
            }
        }
    }
}

@Composable
fun FilterHeader(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val iconTint = Color.Black
    val textColor = MaterialTheme.colorScheme.onSurface

    Row(
        modifier = modifier
            .clickable { onExpandToggle() }
            .background(dividerColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtros",
            style = MaterialTheme.typography.labelMedium,
            color = textColor
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Cerrar menú" else "Abrir menú",
            tint = iconTint
        )
    }
}

@Composable
fun FilterMenuOverlay(
    onFilterSelected: (Boolean) -> Unit,
    onNoteTypeFilterSelected: (NoteType?) -> Unit,
    onChronologicalOrderSelected: () -> Unit,
    onDismiss: () -> Unit,
) {
    val iconTint = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .width(220.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFilterSelected(true)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Orden ascendente",
                        tint = iconTint
                    )
                    Text(
                        text = "Ascendente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onFilterSelected(false)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Orden descendente",
                        tint = iconTint
                    )
                    Text(
                        text = "Descendente",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onChronologicalOrderSelected()
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Orden cronológico",
                        tint = iconTint
                    )
                    Text(
                        text = "Orden cronológico",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    thickness = 1.dp
                )

                NoteType.entries.forEach { noteType ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onNoteTypeFilterSelected(noteType)
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filtrar por $noteType",
                            tint = iconTint
                        )
                        Text(
                            text = noteType.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onNoteTypeFilterSelected(null)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar filtro",
                        tint = iconTint
                    )
                    Text(
                        text = "Limpiar filtro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar notas...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}


@Composable
fun NoteItem(
    note: NoteDTO,
    onClick: () -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDarkMode = isSystemInDarkTheme()
    var isFavorite by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }
    val textColor = if (isDarkMode) Color.White else Color.Black
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val goldColor = Color(0xFFFFD700)
    val pinkColor = Color(0xFFFF4081)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (note.type) {
                            NoteType.EVENT -> R.drawable.ic_note_event
                            NoteType.HISTORICAL -> R.drawable.ic_note_historical
                            NoteType.LANDSCAPE -> R.drawable.ic_note_landscape
                            NoteType.CULTURAL -> R.drawable.ic_note_cultural
                            NoteType.CLASSIC -> R.drawable.ic_note_classic
                            NoteType.FOOD -> R.drawable.ic_note_food
                        }
                    ),
                    contentDescription = "Tipo de nota",
                    modifier = Modifier.size(36.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = note.content ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "By: ${note.ownerUsername ?: "Unknown"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "★ ${note.rating}/10",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            note.rating >= 7.5 -> Color(0xFF4CAF50)
                            note.rating >= 2.5 -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                NoteTypeBadge(type = note.type)
                if (note.type == NoteType.EVENT) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Start: ${note.start?.let { formatDateTime(it) }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "End: ${note.end?.let { formatDateTime(it) }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                }
            }

            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    onFavClick(note.id)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.saved) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorito",
                    tint = if (note.saved) goldColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = {
                    isLiked = !isLiked
                    onLikeClick(note.id)
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like",
                    tint = if (note.liked) pinkColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = dividerColor
        )
    }
}


@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun Preview() {
    NoteListContent(
        notes = listOf(
            NoteDTO(title = "Nota 1", content = "Contenido 1", rating = 10, saved = true),
            NoteDTO(title = "Nota 1", rating = 5),
            NoteDTO(title = "Nota 1", content = "dadadadasdad", type = NoteType.EVENT, liked = true, start = "01/02/2023 02:00", end = "01/02/2023 19:00"),
            NoteDTO(title = "Nota 1"),
            NoteDTO(title = "Nota 1"),
            NoteDTO(title = "Nota 1")
        ),
        searchQuery = "",
        onSearchQueryChange = {},
        onNoteClick = {},
        onFavClick = {},
        isFilterExpanded = false,
        onFilterExpandToggle = {},
        onLikeClick = {},
    )
}
