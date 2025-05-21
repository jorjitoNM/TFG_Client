package com.example.client.ui.noteMap.search


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.client.domain.model.GooglePlaceUi
import com.example.client.ui.common.UiEvent
import com.example.client.ui.navigation.NoteMapDestination

@Composable
fun MapSearchScreen(
    viewModel: MapSearchViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    showSnackbar: (String) -> Unit,
    navController: NavController,
    sharedLocationViewModel: SharedLocationViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(state.aviso) {
        when (val event = state.aviso) {
            is UiEvent.PopBackStack -> {
                onNavigateBack()
                viewModel.handleEvent(MapSearchEvent.AvisoVisto)
            }
            is UiEvent.ShowSnackbar -> {
                showSnackbar(event.message)
                viewModel.handleEvent(MapSearchEvent.AvisoVisto)
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
                navController = navController
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
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> LoadingIndicator()
            state.showEmptyState -> EmptyState()
            else -> PlacesList(
                places = state.results,
                sharedLocationViewModel = sharedLocationViewModel,
                navController = navController
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
private fun PlacesList(
    places: List<GooglePlaceUi>,
    sharedLocationViewModel: SharedLocationViewModel,
    navController: NavController
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
                    sharedLocationViewModel.setLocation(place.lat, place.lng)
                    navController.navigate(NoteMapDestination)
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun PlaceCard(
    place: GooglePlaceUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) Color(0xFF23272F) else Color(0xFFF5F7FA)

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (place.photoUrl != null) {
                AsyncImage(
                    model = place.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = if (isDark) Color(0xFF344055) else Color(0xFFE3F0FF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFF8AB4F8) else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (isDark) Color(0xFFE3F0FF) else Color(0xFF202124)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = place.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Color(0xFFB0B8C1) else Color(0xFF6B7280),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

