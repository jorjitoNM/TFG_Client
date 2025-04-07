package com.example.client.ui.noteMap.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.ui.common.UiEvent
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun NoteMapScreen(
    showSnackbar: (String) -> Unit,
    viewModel: NoteMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(NoteMapEvent.GetNotes)
    }

    uiState.aviso?.let { event ->
        when (event) {
            is UiEvent.ShowSnackbar -> {
                LaunchedEffect(event) {
                    showSnackbar(event.message)
                    viewModel.handleEvent(NoteMapEvent.AvisoVisto)
                }
            }
            else -> {}
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = MapProperties(mapType = MapType.NORMAL)
    ) {
        uiState.notes.forEach { note ->
            val position = LatLng(note.latitude, note.longitude)
            val markerColor = if (note.start != null && note.end != null) {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            } else {
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            }

            Marker(
                state = rememberMarkerState(position = position),
                title = note.title,
                snippet = note.content,
                icon = markerColor
            )
        }
    }
}
