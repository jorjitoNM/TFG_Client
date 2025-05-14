package com.example.client.ui.noteMap.list

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.ui.common.UiEvent
import com.google.android.gms.maps.CameraUpdateFactory
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
    viewModel: NoteMapViewModel = hiltViewModel(),
    onAddNoteClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val defaultLocation = LatLng(0.0, 0.0)
    val defaultZoom = 2f

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.handleEvent(NoteMapEvent.CheckLocationPermission)
        if (isGranted) {
            viewModel.handleEvent(NoteMapEvent.GetCurrentLocation)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(NoteMapEvent.GetNotes)
        viewModel.handleEvent(NoteMapEvent.CheckLocationPermission)

        if (!uiState.hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.handleEvent(NoteMapEvent.GetCurrentLocation)
        }
    }

    LaunchedEffect(uiState.currentLocation) {
        uiState.currentLocation?.let { location ->
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(15f)
                        .build()
                )
            )
        }
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

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = uiState.hasLocationPermission
            ),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                if (uiState.currentLocation == null && !cameraPositionState.isMoving) {
                    cameraPositionState.move(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(defaultLocation)
                                .zoom(defaultZoom)
                                .build()
                        )
                    )
                }
            }
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
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }

        FloatingActionButton(
            onClick = onAddNoteClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir Nota")
        }
    }
}
