package com.example.client.ui.userScreen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.R
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.theme.ClassicDark
import com.example.client.ui.theme.ClassicLight
import com.example.client.ui.theme.CulturalDark
import com.example.client.ui.theme.CulturalLight
import com.example.client.ui.theme.EventDark
import com.example.client.ui.theme.EventLight
import com.example.client.ui.theme.FoodDark
import com.example.client.ui.theme.FoodLight
import com.example.client.ui.theme.HistoricalDark
import com.example.client.ui.theme.HistoricalLight
import com.example.client.ui.theme.LandscapeDark
import com.example.client.ui.theme.LandscapeLight

@Composable
fun UserScreen(
    showSnackbar: (String) -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                onFavClick = { noteId ->
                    val note = uiState.notes.find { it.id == noteId }
                    note?.let {
                        if (it.saved) {
                            viewModel.handleEvent(UserEvent.DelFavNote(noteId))
                        } else {
                            viewModel.handleEvent(UserEvent.FavNote(noteId))
                        }
                    }
                },
                onLikeClick = { noteId ->
                    val note = uiState.notes.find { it.id == noteId }
                    note?.let {
                        if (it.liked) {
                            viewModel.handleEvent(UserEvent.DelLikeNote(noteId))
                        } else {
                            viewModel.handleEvent(UserEvent.LikeNote(noteId))
                        }
                    }
                }
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
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(110.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
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

        when (selectedTab) {
            UserTab.NOTES -> {
                NoteList(
                    notes = notes,
                    onNoteClick = {},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
            }

            UserTab.FAVORITES -> {
                NoteList(
                    notes = notes,
                    onNoteClick = {},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
            }

            UserTab.LIKES -> {
                NoteList(
                    notes = notes,
                    onNoteClick = {},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
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
fun NoteList(
    notes: List<NoteDTO>,
    onNoteClick: (Int) -> Unit,
    onFavClick: (Int) -> Unit,
    onLikeClick: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteCard(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onFavClick = { onFavClick(note.id) },
                    onLikeClick = { onLikeClick(note.id) },
                )
            }
        }
    }
}

@Composable
fun NoteCard(
    note: NoteDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onFavClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black
    val dividerColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFE0E0E0)
    val goldColor = Color(0xFFFFD700)
    val pinkColor = Color(0xFFFF4081)

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
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = when (note.type) {
                            NoteType.EVENT -> R.drawable.ic_note_event
                            NoteType.HISTORICAL -> R.drawable.ic_note_historical
                            NoteType.LANDSCAPE -> R.drawable.ic_note_landscape
                            NoteType.CULTURAL -> R.drawable.ic_note_cultural
                            NoteType.CLASSIC -> R.drawable.ic_note_classic
                            NoteType.FOOD -> R.drawable.ic_note_food
                        }
                    ),
                    contentDescription = "Tipo de nota",
                    modifier = Modifier.size(36.dp)
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = note.content ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "By: ${note.ownerUsername ?: "Unknown"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "★ ${note.rating}/5",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            note.rating >= 4 -> Color(0xFF4CAF50)
                            note.rating >= 2.5 -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        },
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                NoteTypeBadge(type = note.type)

            }

            IconButton(
                onClick = onFavClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.saved) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Favorito",
                    tint = if (note.saved) goldColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (note.liked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Me gusta",
                    tint = if (note.liked) pinkColor else textColor.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = dividerColor
        )
    }
}

@Composable
fun NoteTypeBadge(type: NoteType, modifier: Modifier = Modifier) {
    val color = noteTypeBadgeColor(type)
    val label = when (type) {
        NoteType.CLASSIC -> "CLÁSICA"
        NoteType.EVENT -> "EVENTO"
        NoteType.FOOD -> "GASTRO"
        NoteType.HISTORICAL -> "HISTÓRICA"
        NoteType.LANDSCAPE -> "PAISAJE"
        NoteType.CULTURAL -> "CULTURAL"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}


@Composable
fun noteTypeBadgeColor(type: NoteType): Color {
    val isDark = isSystemInDarkTheme()
    return when (type) {
        NoteType.CLASSIC -> if (isDark) ClassicDark else ClassicLight
        NoteType.EVENT -> if (isDark) EventDark else EventLight
        NoteType.FOOD -> if (isDark) FoodDark else FoodLight
        NoteType.HISTORICAL -> if (isDark) HistoricalDark else HistoricalLight
        NoteType.LANDSCAPE -> if (isDark) LandscapeDark else LandscapeLight
        NoteType.CULTURAL -> if (isDark) CulturalDark else CulturalLight
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
        onFavClick = {},
        onLikeClick = {}
    )
}
