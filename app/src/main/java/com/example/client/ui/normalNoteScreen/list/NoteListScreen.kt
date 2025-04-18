package com.example.client.ui.normalNoteScreen.list

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
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.example.client.ui.common.UiEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType


@Composable
fun NoteListScreen(
    showSnackbar: (String) -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    viewModel: NoteListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
            NoteList(
                notes = state.notes,
                onNoteClick = { noteId ->
                    viewModel.handleEvent(NoteListEvent.SelectedNote(noteId))
                }
            )
        }
    }
}

@Composable
fun NoteList(
    notes: List<NoteDTO>,
    onNoteClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes) { note ->
            NoteItem(note = note, onClick = { onNoteClick(note.id) })
        }
    }
}

@Composable
fun NoteItem(
    note: NoteDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "By: ${note.ownerUsername ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Rating: ${note.rating}/5",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Check if it's an event note and display additional information
            if (note.type == NoteType.EVENT) {
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
                    note.start?.let {
                        Text(
                            text = "Start: ${formatDateTime(it)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    note.end?.let {
                        Text(
                            text = "End: ${formatDateTime(it)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }


                }

            }
        }
    }
}

fun formatDateTime(dateTimeStr: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTime.format(outputFormatter)
    } catch (e: Exception) {
        dateTimeStr
    }
}

@Preview
@Composable
fun NoteListScreenPreview () {
    NoteList(listOf(NoteDTO(),NoteDTO(),NoteDTO()),{})
}