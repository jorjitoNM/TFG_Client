package com.example.client.ui.noteMap.list


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.FilterChip
import com.example.client.ui.common.NotesBottomSheet
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.getMarkerColor
import com.example.client.ui.common.getMarkerIconRes
import com.example.client.ui.common.vectorToBitmap
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteMapScreen(
    showSnackbar: (String) -> Unit,
    viewModel: NoteMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState()
    val defaultLocation = LatLng(0.0, 0.0)
    val defaultZoom = 2f
    val scope = rememberCoroutineScope()
    val notesByLocation = uiState.notes.groupBy { note ->
        LatLng(note.latitude, note.longitude)
    }
    // Search and filter state
    var searchText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<NoteType?>(null) }

    // Bottom sheet state
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    // Selected notes for the bottom sheet
    val selectedNotes = remember { mutableStateListOf<NoteDTO>() }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

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

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            NotesBottomSheet(
                notes = selectedNotes,
                location = selectedLocation
            )
        },
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContainerColor = Color.White,
        sheetShadowElevation = 8.dp
    ) {
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
                // Agrupa las notas por ubicación
                val context = LocalContext.current

                notesByLocation.forEach { (location, notes) ->
                    val markerState = rememberMarkerState(position = location)
                    val note = notes.first()
                    val noteType = note.type
                    val isSelected = selectedLocation == location

                    // Si quieres cambiar el color cuando está seleccionado, puedes combinar así:
                    val iconBitmapDescriptor = if (isSelected) {
                        // Usa un marcador de color si está seleccionado
                        BitmapDescriptorFactory.defaultMarker(getMarkerColor(noteType))
                    } else {
                        // Usa el icono personalizado si NO está seleccionado
                        vectorToBitmap(getMarkerIconRes(noteType), context)
                    }

                    Marker(
                        state = markerState,
                        title = "${notes.size} note(s)",
                        snippet = "Click to view details",
                        icon = iconBitmapDescriptor,
                        onClick = {
                            selectedNotes.clear()
                            selectedNotes.addAll(notes)
                            selectedLocation = location
                            scope.launch { bottomSheetState.expand() }
                            true
                        }
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
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Buscador
                        TextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
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
                            ),
                            shape = RoundedCornerShape(28.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            )
                        )

                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 6.dp),
                        ) {
                            items(NoteType.entries) { type ->
                                FilterChip(
                                    noteType = type,
                                    isSelected = selectedType == type,
                                    onClick = {
                                        selectedType = if (selectedType == type) null else type
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
            }

            // Location button
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
}











