package com.example.client.ui.userScreen.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage

@Composable
fun UserScreen(
    showSnackbar: (String) -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.handleEvent(UserEvent.LoadProfileImage(it))
        }
    }
    LaunchedEffect(Unit) {
        viewModel.handleEvent(UserEvent.LoadUser)
    }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    showSnackbar(event.message)
                    viewModel.handleEvent(UserEvent.AvisoVisto)
                }

                is UiEvent.PopBackStack -> {
                    viewModel.handleEvent(UserEvent.AvisoVisto)
                }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            UserContent(
                notes = uiState.notes,
                user = uiState.user,
                selectedTab = uiState.selectedTab,
                onTabSelected = { tab ->
                    viewModel.handleEvent(UserEvent.SelectTab(tab))
                },
                profileImageUri = uiState.profileImageUri,
                onProfileImageSelected = { pickImageLauncher.launch("image/*") }

            )
        }
    }
}


@Composable
fun UserContent(
    notes: List<NoteDTO>,
    user: UserDTO,
    selectedTab: UserTab,
    onTabSelected: (UserTab) -> Unit,
    profileImageUri: Uri?,
    onProfileImageSelected: () -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        imageUri = uri
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Card con información del usuario
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .clickable(onClick = onProfileImageSelected),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri != null) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.size(110.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = user.username,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = user.rol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .width(120.dp)
                ) {
                    Text(
                        text = "Seguir",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    UserStat(number = user.notes.size, label = "Posts")
                    UserStat(number = user.followers.size, label = "Seguidores")
                    UserStat(number = user.following.size, label = "Siguiendo")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TabSelector(
            selectedTab = selectedTab,
            onTabSelected = onTabSelected,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido según pestaña
        when (selectedTab) {
            UserTab.NOTES -> {
                Text("Notas propias", modifier = Modifier.padding(16.dp))
            }
            UserTab.FAVORITES -> {
                NoteSavedList(
                    notes = notes,
                    onNoteClick = {},
                    onFavClick = {}
                )
            }
            UserTab.LIKES -> {
                Text("Notas con likes", modifier = Modifier.padding(16.dp))
            }
        }
    }
}



@Composable
fun UserStat(number: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 12.dp)
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun TabSelector(
    selectedTab: UserTab,
    onTabSelected: (UserTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {

            TabButton(
                text = "Notas",
                isSelected = selectedTab == UserTab.NOTES,
                onClick = { onTabSelected(UserTab.NOTES) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Favoritos",
                isSelected = selectedTab == UserTab.FAVORITES,
                onClick = { onTabSelected(UserTab.FAVORITES) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Likes",
                isSelected = selectedTab == UserTab.LIKES,
                onClick = { onTabSelected(UserTab.LIKES) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            },
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun NoteSavedList(
    notes: List<NoteDTO>,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onFavClick = { onFavClick(note.id) }
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    note: NoteDTO,
    onClick: () -> Unit,
    onFavClick: (Int) -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(false) }

    val goldColor = Color(0xFFFFD700)
    val pinkColor = Color(0xFFFF4081)
    val textColor = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = note.content ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor.copy(alpha = 0.8f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "By: ${note.ownerUsername ?: "Unknown"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.6f)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = when {
                                    note.rating >= 8 -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    note.rating >= 5 -> Color(0xFFFFC107).copy(alpha = 0.2f)
                                    else -> Color(0xFFF44336).copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "★ ${note.rating}/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                note.rating >= 8 -> Color(0xFF4CAF50)
                                note.rating >= 5 -> Color(0xFFFFC107)
                                else -> Color(0xFFF44336)
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                if (note.type == NoteType.EVENT) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                        Text(
                            text = "Start: ${note.start?.let { formatDateTime(it) }}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "End: ${note.end?.let { formatDateTime(it) }}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = {
                        isFavorite = !isFavorite
                        onFavClick(note.id)
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (note.saved) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorito",
                        tint = if (note.saved) goldColor else textColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = { isLiked = !isLiked },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (note.liked) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = "Me gusta",
                        tint = if (note.liked) pinkColor
                        else textColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
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

@Preview(name = "Portrait Mode", showBackground = true, device = Devices.PHONE)
@Composable
fun Preview() {
    UserContent(
        notes = listOf(
            NoteDTO(
                title = "sdadasdadadadadadad",
                content = "dsadadadadad",
                rating = 10,
                ownerUsername = "dad"
            ),
            NoteDTO(rating = 5),
            NoteDTO(type = NoteType.EVENT, start = "2q", end = "dad"),
            NoteDTO(),
            NoteDTO(),
            NoteDTO()
        ),
        user = UserDTO(),
        onTabSelected = {},
        selectedTab = UserTab.FAVORITES,
        profileImageUri = null,
        onProfileImageSelected = {}
    )
}