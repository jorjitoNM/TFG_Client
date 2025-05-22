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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.client.data.model.UserDTO
import com.example.client.ui.common.UiEvent

@Composable
fun UserSearchScreen(
    viewModel: UserSearchViewModel = hiltViewModel(),
    showSnackbar: (String) -> Unit,
    navController: NavController
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

    Column(
        modifier = Modifier
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
                onValueChange = { viewModel.handleEvent(UserSearchEvent.UpdateSearchText(it)) },
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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
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
            if (uiState.showEmptyState && !uiState.isLoading) {
                Text(
                    text = "No se encontraron usuarios",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(uiState.users) { user ->
                    UserCard(user = user, onClick = {

                    })
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
    onClick: () -> Unit
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

                // Solo si es premium, muestra el rol destacado
                if (user.rol == "PREMIUM") {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background( Color(0xFF4CAF50))
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
        }

        Divider(
            color = dividerColor,
            thickness = 1.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


