package com.example.client.ui.noteMap.search


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.client.R
import com.example.client.domain.model.google.*
import com.example.client.ui.common.UiEvent
import com.example.client.ui.navigation.NoteMapDestination
import kotlinx.coroutines.delay


@Composable
fun MapSearchScreen(
    viewModel: MapSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    navController: NavController,
    showSnackbar: (String) -> Unit,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(state.aviso) {
        state.aviso?.let {
            when (it) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(it.message)
                    viewModel.handleEvent(MapSearchEvent.AvisoVisto)
                }
                is UiEvent.PopBackStack -> {
                    onNavigateBack()
                    viewModel.handleEvent(MapSearchEvent.AvisoVisto)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            SearchBarSection(
                state = state,
                focusRequester = focusRequester,
                onSearchTextChanged = { viewModel.handleEvent(MapSearchEvent.UpdateSearchText(it)) },
                onNavigateBack = { viewModel.handleEvent(MapSearchEvent.NavigateBack) }
            )

            ResultsSection(
                state = state,
                sharedLocationViewModel = sharedLocationViewModel,
                navController = navController,
                onInsertRecent = { location ->
                    viewModel.handleEvent(
                        MapSearchEvent.InsertRecent(location, viewModel.getLoggedUser())
                    )
                },
                onDeleteRecent = { location ->
                    viewModel.handleEvent(
                        MapSearchEvent.DeleteRecent(location.id, location.userLogged)
                    )
                }
            )
        }
    }
}

@Composable
private fun SearchBarSection(
    state: MapSearchState,
    focusRequester: FocusRequester,
    onSearchTextChanged: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(1f)
    ) {
        TextField(
            value = state.searchText,
            onValueChange = onSearchTextChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .height(56.dp)
                .focusRequester(focusRequester),
            placeholder = { Text("Buscar lugares...") },
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            },
            trailingIcon = {
                Icon(Icons.Default.Search, "Search Icon")
            },
            shape = RoundedCornerShape(28.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun ResultsSection(
    state: MapSearchState,
    sharedLocationViewModel: SharedLocationViewModel,
    navController: NavController,
    onInsertRecent: (Location) -> Unit,
    onDeleteRecent: (Location) -> Unit // <--- Añade esto
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingIndicator()
            state.showEmptyState -> EmptyState()
            state.searchText.isBlank() -> RecentsList(
                recents = state.recents,
                onRecentClick = { place ->
                    onInsertRecent(place)
                    sharedLocationViewModel.setLocation(place.lat, place.lng)
                    navController.navigate(NoteMapDestination)
                },
                onDeleteRecent = onDeleteRecent
            )
            else -> PlacesList(
                places = state.results,
                sharedLocationViewModel = sharedLocationViewModel,
                navController = navController,
                onInsertRecent = onInsertRecent,
                isSearchTextBlank = state.searchText.isBlank()
            )
        }
    }
}


@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = 4.dp)
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("No se encontraron resultados", color = Color.Gray)
    }
}
@Composable
fun ExpandableAddress(
    address: String,
    secondaryTextColor: Color,
    collapsedMaxLines: Int = 2,
) {
    val expanded by remember { mutableStateOf(false) }
    var isOverflow by remember { mutableStateOf(false) }
    var copied by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Temporizador para ocultar el mensaje tras 3 segundos
    LaunchedEffect(copied) {
        if (copied) {
            delay(3000)
            copied = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = "Ubicación",
                tint = secondaryTextColor,
                modifier = Modifier
                    .padding(top = 2.dp, end = 6.dp)
                    .size(20.dp)
            )
            Column(modifier = Modifier.weight(1f).padding(top = 2.dp)) {
                Text(
                    text = address,
                    color = secondaryTextColor,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { result -> isOverflow = result.hasVisualOverflow },
                    modifier = Modifier.animateContentSize()
                )
                if (isOverflow || expanded) {
                    TextButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Dirección", address)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = if (expanded) "Ver menos" else "Ver más",
                            color = secondaryTextColor,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
        if (address.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Dirección", address)
                        clipboard.setPrimaryClip(clip)
                        copied = true
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.copy_icon),
                        contentDescription = "Copiar dirección",
                        tint = secondaryTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Copiar dirección",
                        color = secondaryTextColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            // Mensaje animado debajo del botón
            AnimatedVisibility(
                visible = copied,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = "¡Copiado al portapapeles!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(end = 8.dp, top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun PlaceCard(
    place: Location,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedPhotoReference by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black
    val secondaryTextColor = if (isDarkMode) Color.LightGray else Color.DarkGray
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val openColor = Color(0xFF4CAF50)
    val closedColor = Color.Red
    val apiKey = stringResource(R.string.google_maps_key)
    // Dialog para la imagen ampliada
    if (selectedPhotoReference != null) {
        PlaceImageDialog(
            place = place,
            selectedPhotoReference = selectedPhotoReference!!,
            onDismiss = { selectedPhotoReference = null },
            apiKey = apiKey,
            secondaryTextColor = secondaryTextColor,
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (place.openingHours != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = place.openingHours,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (place.openingHours.contains("Abierto", ignoreCase = true))
                            openColor else closedColor
                    )
                }
            }

            // Expand/Collapse icon
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = secondaryTextColor
                )
            }
        }

        // Expanded content
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 64.dp, end = 16.dp, bottom = 16.dp)
            ) {
                // Photos gallery
                if (place.photos.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(place.photos) { photo ->
                            AsyncImage(
                                model = getGooglePhotoUrl(
                                    photo.photoReference,
                                    apiKey,
                                    maxWidth = 200 // Miniatura
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(end = 8.dp)
                                    .clickable { selectedPhotoReference = photo.photoReference }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Website
                // Phone number
                if (!place.phoneNumber.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Teléfono",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = place.phoneNumber,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${place.phoneNumber}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }


                // Rating
                if (place.rating != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${place.rating} (${place.userRatingsTotal ?: 0} reseñas)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = textColor
                        )
                    }
                }
            }
        }

        // Divider
        Divider(
            color = dividerColor,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Composable
private fun PlacesList(
    places: List<Location>,
    sharedLocationViewModel: SharedLocationViewModel,
    navController: NavController,
    onInsertRecent: (Location) -> Unit,
    isSearchTextBlank: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(places) { place ->
            PlaceCard(
                place = place,
                onClick = {
                        onInsertRecent(place)
                    sharedLocationViewModel.setLocation(place.lat, place.lng)
                    navController.navigate(NoteMapDestination)
                },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PlaceImageDialog(
    place: Location,
    selectedPhotoReference: String,
    onDismiss: () -> Unit,
    apiKey: String,
    secondaryTextColor: Color,
) {
    val isDarkMode = isSystemInDarkTheme()
    val dialogBackground = if (isDarkMode) Color(0xFF232323) else Color.White
    val headerBackground = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val titleColor = if (isDarkMode) Color.White else Color.Black

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(dialogBackground)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(headerBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = titleColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = place.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = titleColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = titleColor
                            )
                        }
                    }
                    AsyncImage(
                        model = getGooglePhotoUrl(
                            selectedPhotoReference,
                            apiKey,
                            maxWidth = 3000
                        ),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(dialogBackground)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        ExpandableAddress(
                            address = place.address,
                            secondaryTextColor = secondaryTextColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentPlaceCard(
    place: Location,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black
    val secondaryTextColor = if (isDarkMode) Color.LightGray else Color.DarkGray
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono de mapa
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Nombre y dirección
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onClick() }
        ) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = place.address,
                style = MaterialTheme.typography.bodyMedium,
                color = secondaryTextColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Botón de eliminar a la derecha
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Eliminar lugar",
                tint = if (isDarkMode) Color.LightGray else Color.DarkGray
            )
        }
    }
    Divider(
        color = dividerColor,
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun RecentsList(
    recents: List<Location>,
    onRecentClick: (Location) -> Unit,
    onDeleteRecent: (Location) -> Unit
) {
    if (recents.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn, // Icono de lugar
                    contentDescription = "Sin lugares recientes",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "No hay búsquedas recientes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.95f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Cuando busques o selecciones lugares aparecerán aquí.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(recents) { place ->
                RecentPlaceCard(
                    place = place,
                    onClick = { onRecentClick(place) },
                    onDelete = { onDeleteRecent(place) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}



















