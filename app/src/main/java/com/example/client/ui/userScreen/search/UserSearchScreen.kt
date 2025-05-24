package com.example.client.ui.userScreen.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

@Composable
fun UserSearchScreen(
    viewModel: UserSearchViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            if (event is UiEvent.ShowSnackbar) {
                showSnackbar(event.message)
                viewModel.handleEvent(UserSearchEvent.AvisoVisto)
            }
        }
    }

    UserSearchScreenContent(
        uiState = uiState,
        onSearchTextChanged = { viewModel.handleEvent(UserSearchEvent.UpdateSearchText(it)) },
        onUserClick = { viewModel.handleEvent(UserSearchEvent.UserClicked(it)) },
        onUserDelete = { viewModel.handleEvent(UserSearchEvent.OnDeleteUser(it)) },
        focusRequester = focusRequester
    )

}

@Composable
fun UserSearchScreenContent(
    uiState: UserSearchState,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onSearchTextChanged: (String) -> Unit = {},
    onUserClick: (UserDTO) -> Unit = {},
    onUserDelete: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            TextField(
                value = uiState.searchText,
                onValueChange = onSearchTextChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .height(56.dp)
                    .focusRequester(focusRequester),
                placeholder = { Text("Buscar usuarios...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                },
                keyboardActions = KeyboardActions(),
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    strokeWidth = 4.dp
                )
            }
            else if (uiState.showEmptyState && uiState.searchText.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar usuarios",
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
                            text =
                                "Cuando busques o selecciones usuarios aparecerán aquí.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                    }
                }
            }

            // Estado vacío: con texto (búsqueda sin resultados)
            else if (uiState.showEmptyState && uiState.searchText.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "No encontrado",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "No hemos encontrado ningún usuario",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.95f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Intenta buscar con otro nombre o revisa la ortografía.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(uiState.users) { user ->
                    UserCard(
                        user = user,
                        onClick = { onUserClick(user) },
                        onDelete = if (uiState.searchText.isBlank())
                        { { onUserDelete(user.username) } }
                        else null
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

        }
    }
}


@Composable
fun UserCard(
    user: UserDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: (() -> Unit)? = null // Nuevo parámetro opcional
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val iconBackground = if (isDarkMode) Color(0xFF333333) else Color(0xFFF0F0F0)

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
            // Icono de usuario
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBackground,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Usuario",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Contenido principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )

                // Solo si es premium, muestra el badge
                if (user.rol == "PREMIUM") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF4CAF50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }
                }
            }

            // Icono de eliminar (solo si tiene callback)
            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar usuario",
                        tint = if (isDarkMode) Color.LightGray else Color.DarkGray
                    )
                }
            }
        }

        Divider(
            color = dividerColor,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


class UserSearchStateProvider : PreviewParameterProvider<UserSearchState> {
    override val values = sequenceOf(
        // Estado: lista vacía, sin loading (pantalla de lupa)
        UserSearchState(
            users = emptyList(),
            isLoading = false,
            searchText = "",
            showEmptyState = true
        ),
        // Estado: cargando
        UserSearchState(
            users = emptyList(),
            isLoading = true,
            searchText = "",
            showEmptyState = false
        ),
        // Estado: con usuarios encontrados
        UserSearchState(
            users = listOf(
                UserDTO(username = "alice", rol = "PREMIUM"),
                UserDTO(username = "bob", rol = "FREE"),
                UserDTO(username = "charlie", rol = "PREMIUM")
            ),
            isLoading = false,
            searchText = "a",
            showEmptyState = false
        ),
        // Estado: búsqueda sin resultados
        UserSearchState(
            users = emptyList(),
            isLoading = false,
            searchText = "usuarioquenoexiste",
            showEmptyState = true
        ),
        UserSearchState(
            users = emptyList(),
            isLoading = false,
            searchText = "",
            showEmptyState = true,
        ),UserSearchState(
            users = listOf(
                UserDTO(username = "alice", rol = "PREMIUM"),
                UserDTO(username = "bob", rol = "FREE"),
                UserDTO(username = "charlie", rol = "PREMIUM")
            ),
            isLoading = false,
            searchText = "",
            showEmptyState = false
        )
    )
}

@Preview(showBackground = true)
@Composable
fun UserSearchScreenPreview(
    @PreviewParameter(UserSearchStateProvider::class) uiState: UserSearchState
) {
    UserSearchScreenContent(uiState = uiState)
}


