package com.example.client.ui.noteMap.list


import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.composables.FilterChip
import com.example.client.ui.common.composables.NotesBottomSheet
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.getMarkerColor
import com.example.client.ui.common.composables.getMarkerIconRes
import com.example.client.ui.common.composables.vectorToBitmap
import com.example.client.ui.noteMap.search.SharedLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteMapScreen(
    showSnackbar: (String) -> Unit,
    viewModel: NoteMapViewModel = hiltViewModel(),
    sharedLocationViewModel: SharedLocationViewModel,
    onNavigateToList: () -> Unit,
    onAddNoteClick: () -> Unit


) {
    val latLong by sharedLocationViewModel.selectedLocation.collectAsState()
    val sharedNoteType by sharedLocationViewModel.selectedNoteType.collectAsState()

    val initialLat = latLong?.first
    val initialLon = latLong?.second
    val isDarkMode = isSystemInDarkTheme()


    var moveToCurrentLocation by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(sharedNoteType) {
        // Solo actualiza si el filtro cambió y no es igual al actual
        if (sharedNoteType != uiState.selectedType) {
            viewModel.handleEvent(NoteMapEvent.FilterByType(sharedNoteType))
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(initialLat ?: 0.0, initialLon ?: 0.0),
            if (initialLat != null && initialLon != null) 15f else 2f
        )
    }
    var cameraMoved by remember { mutableStateOf(false) }
    val defaultLocation = LatLng( 0.0,  0.0)
    val defaultZoom =  2f
    // Al cargar, mueve la cámara si hay coordenadas iniciales
    LaunchedEffect(initialLat, initialLon) {
        if (!cameraMoved && initialLat != null && initialLon != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder()
                        .target(LatLng(initialLat, initialLon))
                        .zoom(15f)
                        .build()
                )
            )
            cameraMoved = true
        }
    }




    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val surfaceColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val fabContainerColor = if (isDarkMode) Color(0xFF23272F) else Color.White
    val fabContentColor = if (isDarkMode) Color.White else Color.Black
    val bottomSheetColor = if (isDarkMode) Color(0xFF23272F) else Color.White

    // Bottom sheet state
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Hidden,
        skipHiddenState = false
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)



    val darkStyle = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
    }
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

    // Lanzar eventos iniciales
    LaunchedEffect(Unit) {
        viewModel.handleEvent(NoteMapEvent.GetNotes)
        viewModel.handleEvent(NoteMapEvent.CheckLocationPermission)
    }

    // Solicitar permisos si no los tiene
    LaunchedEffect(uiState.hasLocationPermission) {
        if (!uiState.hasLocationPermission) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            viewModel.handleEvent(NoteMapEvent.GetCurrentLocation)
        }
    }

    // Restaurar la posición de la cámara si hay guardada
    LaunchedEffect(uiState.cameraLatLng, uiState.cameraZoom) {
        if (uiState.cameraZoom != null) {
            uiState.cameraLatLng?.let {
                CameraPosition.Builder()
                    .target(it)
                    .zoom(uiState.cameraZoom!!)
                    .build()
            }?.let {
                CameraUpdateFactory.newCameraPosition(
                    it
                )
            }?.let {
                cameraPositionState.move(
                    it
                )
            }
        }
    }

    // Guardar la posición de la cámara cuando cambia
    LaunchedEffect(cameraPositionState.position) {
        if (!cameraPositionState.isMoving) {
            viewModel.handleEvent(
                NoteMapEvent.SaveCameraPosition(
                    latLng = cameraPositionState.position.target,
                    zoom = cameraPositionState.position.zoom
                )
            )
        }
    }

    // Si no hay posición guardada, mover a la ubicación actual
    LaunchedEffect(uiState.currentLocation) {
        if (uiState.cameraLatLng == null) {
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
    }

    val filterKey = remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.selectedType) {
        filterKey.intValue += 1
    }

    // Filtrado de notas por tipo y búsqueda
    val filteredNotes = remember(uiState.notes, uiState.selectedType, uiState.currentSearch, filterKey.intValue) {
        uiState.notes
            .filter { note ->
                (uiState.selectedType == null || note.type == uiState.selectedType) &&
                        (uiState.currentSearch.isBlank() ||
                                note.title.contains(uiState.currentSearch, ignoreCase = true) ||
                                (note.content?.contains(uiState.currentSearch, ignoreCase = true) ?: false))
            }
    }
    val notesByLocation = remember(filteredNotes, filterKey.intValue) {
        filteredNotes.groupBy { note -> LatLng(note.latitude, note.longitude) }
    }

    // Mostrar Snackbar si hay aviso
    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(event.message)
                    viewModel.handleEvent(NoteMapEvent.AvisoVisto)
                }
                is UiEvent.PopBackStack -> {
                    onNavigateToList()
                    viewModel.handleEvent(NoteMapEvent.AvisoVisto)
                }

            }
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
        sheetContainerColor = bottomSheetColor,
        sheetShadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Map content
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                properties = MapProperties(
                    mapType = MapType.NORMAL,
                    isMyLocationEnabled = uiState.hasLocationPermission,
                    mapStyleOptions = if (isDarkMode) darkStyle else null
                ),
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    mapToolbarEnabled = false
                )
                ,
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    if (uiState.currentLocation == null && !cameraPositionState.isMoving && uiState.cameraLatLng == null) {
                        cameraPositionState.move(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.Builder()
                                    .target(defaultLocation)
                                    .zoom(defaultZoom)
                                    .build()
                            )
                        )
                    }
                },
                onMapClick = { _ ->
                    // Limpiar selección
                    selectedNotes.clear()
                    selectedLocation = null
                    // Cerrar el bottom sheet
                    scope.launch {
                        bottomSheetState.hide()
                    }
                }

            ) {
                key(filterKey.intValue) {
                    notesByLocation.forEach { (location, notes) ->
                        val markerState = rememberMarkerState(position = location)
                        val note = notes.first()
                        val noteType = note.type
                        val isSelected = selectedLocation == location

                        val iconBitmapDescriptor = when {
                            notes.size > 1 && isSelected ->
                                BitmapDescriptorFactory.defaultMarker(210f) // Gris aproximado
                            notes.size > 1 ->
                                vectorToBitmap(R.drawable.ic_note_multinote, context)
                            isSelected ->
                                BitmapDescriptorFactory.defaultMarker(getMarkerColor(noteType))
                            else ->
                                vectorToBitmap(getMarkerIconRes(noteType), context)
                        }

                        Marker(
                            state = markerState,
                            icon = iconBitmapDescriptor,
                            onClick = {
                                selectedNotes.clear()
                                selectedNotes.addAll(notes)
                                selectedLocation = location
                                scope.launch { bottomSheetState.expand() }
                                false
                            }
                        )
                    }
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
                    color = surfaceColor,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Search field
                        TextField(
                            value = uiState.currentSearch,
                            onValueChange = {
                                viewModel.handleEvent(NoteMapEvent.UpdateSearchText(it))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .height(56.dp)
                                .clickable(
                                    indication = null, // Quita el ripple
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    viewModel.handleEvent(NoteMapEvent.NavigateToSearch)
                                },
                            enabled = false, // Deshabilita edición directa aquí
                            placeholder = { Text("Buscar lugares...") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    tint = Color.DarkGray
                                )
                            },
                            trailingIcon = {
                                if (uiState.currentSearch.isNotEmpty()) {
                                    IconButton(onClick = {
                                        viewModel.handleEvent(NoteMapEvent.UpdateSearchText(""))
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
                                    if (uiState.currentSearch.isNotEmpty()) {
                                        viewModel.handleEvent(NoteMapEvent.SearchNote(uiState.currentSearch))
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

                        // Note type filters
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 6.dp),
                        ) {
                            items(NoteType.entries) { type ->
                                FilterChip(
                                    noteType = type,
                                    isSelected = uiState.selectedType == type,
                                    onClick = {
                                        val newType = if (uiState.selectedType == type) null else type
                                        viewModel.handleEvent(NoteMapEvent.FilterByType(newType))
                                        sharedLocationViewModel.setNoteType(newType)
                                    },
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
                    onClick = { viewModel.handleEvent(NoteMapEvent.GetCurrentLocation)
                        moveToCurrentLocation = true},
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    containerColor = fabContainerColor,
                    contentColor =  fabContentColor
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_map_icon),
                        contentDescription = "Google Maps Pin",
                        modifier = Modifier.size(32.dp)
                    )
                }

                FloatingActionButton(
                    onClick = {

                        uiState.currentLocation?.let { location ->
                            sharedLocationViewModel.setLocation(location.latitude, location.longitude)
                        }

                        onAddNoteClick()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    containerColor = Color(0xFF2196F3),
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Nota")
                }
            }

            LaunchedEffect(uiState.currentLocation, moveToCurrentLocation) {
                if (moveToCurrentLocation && uiState.currentLocation != null) {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition.Builder()
                                .target(
                                    LatLng(
                                        uiState.currentLocation!!.latitude,
                                        uiState.currentLocation!!.longitude
                                    )
                                )
                                .zoom(15f)
                                .build()
                        )
                    )
                    moveToCurrentLocation = false
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                )
            }


        }
    }

}