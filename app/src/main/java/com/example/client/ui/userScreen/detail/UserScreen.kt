package com.example.client.ui.userScreen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.client.data.model.NoteDTO
import com.example.client.data.model.UserDTO
import com.example.client.domain.model.note.NoteType
import com.example.client.ui.common.UiEvent
import com.example.client.ui.common.composables.NoteList
import com.example.client.ui.common.composables.UserStat
import com.example.client.ui.userScreen.DetailNavigationEvent

@Composable
fun UserScreen(
    showSnackbar: (String) -> Unit,
    onToggleTheme: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateToNoteDetail: (Int) -> Unit,
    onNavigateToDetailObservable : (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(UserEvent.LoadUser)
        viewModel.handleEvent(UserEvent.GetFollowers)
        viewModel.handleEvent(UserEvent.GetFollowing)
    }

    LaunchedEffect(uiState.aviso) {
        uiState.aviso?.let { event ->
            if (event is UiEvent.ShowSnackbar) {
                    showSnackbar(event.message)
                    viewModel.handleEvent(UserEvent.AvisoVisto)
            }
        }
    }

    LaunchedEffect(uiState.navigationEvent) {
        when (val event = uiState.navigationEvent) {
            is DetailNavigationEvent.NavigateToMyNoteDetail -> {
                onNavigateToNoteDetail(event.noteId)
                viewModel.handleEvent(UserEvent.NavigationConsumed)
            }
            is DetailNavigationEvent.NavigateToNormalNoteDetail -> {
                onNavigateToDetailObservable(event.noteId)
                viewModel.handleEvent(UserEvent.NavigationConsumed)
            }
            else -> {}
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
            Column(modifier = Modifier.fillMaxSize()) {
                ThemeToggleSwitch(
                    isDarkTheme = isDarkTheme,
                    onToggle = onToggleTheme,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(16.dp)
                )

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
            UserContent(
                notes = uiState.notes,
                user = uiState.user,
                followers = uiState.followers,
                following = uiState.following,
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
                },
                onNoteClick = { noteId ->
                    val isMyNote = uiState.selectedTab == UserTab.NOTES
                    viewModel.handleEvent(UserEvent.SelectedNote(noteId, isMyNote))
                },
            )
        }
    }
}


@Composable
fun ThemeToggleSwitch(
    isDarkTheme: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isDarkTheme) "Modo Oscuro" else "Modo Claro",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.width(8.dp))
        androidx.compose.material3.Switch(
            checked = isDarkTheme,
            onCheckedChange = onToggle
        )
    }
}
@Composable
fun UserContent(
    notes: List<NoteDTO>,
    user: UserDTO,
    followers: List<UserDTO>,
    following: List<UserDTO>,
    selectedTab: UserTab,
    onNoteClick: (Int) -> Unit,
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 45.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    UserStat(number = user.notes.size ?: 0, label = "Posts")
                    Spacer(Modifier.width(32.dp))
                    UserStat(number = followers.size, label = "Followers")
                    Spacer(Modifier.width(32.dp))
                    UserStat(number = following.size, label = "Following")
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
                    onNoteClick = {onNoteClick(it)},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
            }

            UserTab.FAVORITES -> {
                NoteList(
                    notes = notes,
                    onNoteClick = {onNoteClick(it)},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
            }

            UserTab.LIKES -> {
                NoteList(
                    notes = notes,
                    onNoteClick = {onNoteClick(it)},
                    onFavClick = onFavClick,
                    onLikeClick = onLikeClick
                )
            }
        }
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
                text = "Notes",
                isSelected = selectedTab == UserTab.NOTES,
                onClick = { onTabSelected(UserTab.NOTES) },
                modifier = Modifier.weight(1f)
            )
            TabButton(
                text = "Favorites",
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
        onLikeClick = {},
        followers = emptyList(),
        following = emptyList(),
        onNoteClick = {},
    )
}
