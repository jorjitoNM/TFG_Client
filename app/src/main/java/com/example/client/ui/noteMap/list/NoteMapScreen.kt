package com.example.client.ui.noteMap.list

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.ui.text.input.ImeAction


import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
    viewModel: NoteMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val defaultLocation = LatLng(0.0, 0.0)
    val defaultZoom = 2f

    // Search state
    var searchText by remember { mutableStateOf("") }

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
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Map content
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

        // Floating search bar on top of everything
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        placeholder = { Text("Buscar notas...") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        },
                        trailingIcon = {
                            if (searchText.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchText = ""
                                    viewModel.handleEvent(NoteMapEvent.GetNotes)
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchText.isNotEmpty()) {
                                    viewModel.handleEvent(NoteMapEvent.SearchNote(searchText))
                                }
                            }
                        )
                    )
                }
            }
        }

        if (uiState.hasLocationPermission) {
            FloatingActionButton(
                onClick = { viewModel.handleEvent(NoteMapEvent.GetCurrentLocation) },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "My Location"
                )
            }
        }


        // Loading indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}





